package cn.geeklemon.server.auto;

import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.util.PrimitiveTypeUtil;
import cn.geeklemon.core.util.ReflectUtils;
import cn.geeklemon.server.TemporaryDataHolder;
import cn.geeklemon.server.common.PathUtil;
import cn.geeklemon.server.common.RenderType;
import cn.geeklemon.server.common.RequestMethod;
import cn.geeklemon.server.config.ServerConfig;
import cn.geeklemon.server.context.WebContext;
import cn.geeklemon.server.controller.ControllerDefine;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.controller.annotation.Param;
import cn.geeklemon.server.filter.WebAppFilter;
import cn.geeklemon.server.filter.WebFilter;
import cn.geeklemon.server.intercepter.WebAppInterceptor;
import cn.geeklemon.server.intercepter.WebInterceptor;
import cn.geeklemon.server.multipart.MultiFileUtil;
import cn.geeklemon.server.multipart.annotation.MultiFile;
import cn.geeklemon.server.request.RequestDefine;
import cn.geeklemon.server.response.HttpResponse;
import cn.geeklemon.server.viewrender.ModelAndView;
import cn.geeklemon.server.viewrender.ViewEngineConfig;
import cn.geeklemon.server.websocket.context.WebSocketContext;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.template.Engine;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class AutoWebContext implements WebContext {

    private static final FIFOCache<Object, Object> controllerCache = CacheUtil.newFIFOCache(100);

    private static Map<String, ControllerDefine> controllerMap = new HashMap<>();

    private static final Set<ControllerDefine> controllerSet = new ConcurrentHashSet<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoWebContext.class);
    /**
     * controller匹配时这些参数是不需要验证的
     */
    private static Set<Class<?>> requestParamAssigned = new HashSet<>();

    static {
        requestParamAssigned.add(HttpRequest.class);
        requestParamAssigned.add(io.netty.handler.codec.http.HttpResponse.class);
        requestParamAssigned.add(cn.geeklemon.server.request.HttpRequest.class);
        requestParamAssigned.add(cn.geeklemon.server.response.HttpResponse.class);
    }

    public static void addController(ControllerDefine define) {
        controllerSet.add(define);
        controllerMap.put(define.getPathMatcher(), define);
    }

    private static boolean checkNamedParam(Iterable<String> paramRequired) {
        /*
          重新获取，刷新url中的参数
         */
        cn.geeklemon.server.request.HttpRequest httpRequest = TemporaryDataHolder.loadLemonRequest();
        for (String s : paramRequired) {
            if (ObjectUtil.isNull(httpRequest.getParameter(s))) {
                LOGGER.warn("{} 至少缺少一个参数：{}", httpRequest.URI(), s);
                return false;
            }
        }
        return true;
    }

    public static boolean containParamRegular(Class<?> cls) {
        return requestParamAssigned.contains(cls);
    }

    public static Object convertByClone(Object source, Class<?> toType, Object... params) {

        /**
         * 如果对象本身已经是所指定的类型则不进行转换直接返回 如果对象能够被复制，则返回复制后的对象
         */
        if (toType.isInstance(source)) {
            if (source instanceof Cloneable) {
                if (source.getClass().isArray() && source.getClass().getComponentType() == String.class) {
                    // 字符串数组虽然是Cloneable的子类，但并没有clone方法
                    return source;
                }
                try {
                    Method m = source.getClass().getDeclaredMethod("clone", new Class[0]);
                    m.setAccessible(true);
                    return m.invoke(source, new Object[0]);
                } catch (Exception e) {
                    LOGGER.debug("Can not clone object " + source, e);
                }
            }

            return source;
        }
        return source;
    }

    private static List<Class> getMethodGenericParameterTypes(Method method, int index) {
        List<Class> results = new ArrayList<Class>();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (index >= genericParameterTypes.length || index < 0) {
            throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
        }
        Type genericParameterType = genericParameterTypes[index];
        if (genericParameterType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericParameterType;
            Type[] parameterArgTypes = aType.getActualTypeArguments();
            for (Type parameterArgType : parameterArgTypes) {
                Class parameterArgClass = (Class) parameterArgType;
                results.add(parameterArgClass);
            }
            return results;
        }
        return results;
    }

    /**
     * GET 参数解析 有@param注解的参数
     */
    @SuppressWarnings({"unchecked"})
    private static Object parseParameter(Map<String, List<String>> paramMap, Class<?> type, Param param, Method method,
                                         int index) throws InstantiationException, IllegalAccessException {
        Object value = null;
        String key = param.key();// @Param注解的值
        String defaultValue = param.defaultValue();
        if (key.length() > 0) {
            // 如果参数是map类型
            if (Map.class.isAssignableFrom(type)) {

                Map<String, String> valueMap = new HashMap<String, String>(paramMap.size());
                for (Map.Entry<String, List<String>> entry : paramMap.entrySet()) {
                    List<String> valueList = entry.getValue();
                    valueMap.put(entry.getKey(), valueList.get(0));
                }
                value = valueMap;
            } else {
                List<String> params = paramMap.get(key);
                if (params != null) {
                    // 基础类型
                    if (PrimitiveTypeUtil.isPriType(type)) {
                        value = Convert.convert(type, params.get(0));

                        // 数组
                    } else if (type.isArray()) {
                        String[] strArray = params.toArray(new String[]{});
                        value = convertByClone(strArray, type);

                        // List
                    } else if (List.class.isAssignableFrom(type)) {
                        List<Object> list;
                        List<Class> types = getMethodGenericParameterTypes(method, index);
                        Class<?> listType = types.size() == 1 ? types.get(0) : String.class;
                        if (List.class == type) {
                            list = new ArrayList<Object>();
                        } else {
                            list = (List<Object>) type.newInstance();
                        }
                        for (int i = 0; i < params.size(); i++) {
                            if (params.get(i).length() > 0) {
                                list.add(Convert.convert(listType, params.get(i)));
                            }
                        }
                        value = list;
                    }
                } else {
                    if (PrimitiveTypeUtil.isPriType(type)) {
                        value = Convert.convert(type, defaultValue);
                    }
                }
            }
        }
        return value;
    }

    private ApplicationContext applicationContext;

    private Engine engine;

    private List<WebFilter> filterList = null;

    private List<WebInterceptor> interceptorList = null;

    private ServerConfig serverConfig;

    private ViewEngineConfig viewEngineConfig;

    private WebSocketContext webSocketContext;

    public AutoWebContext(ApplicationContext context) {
        this.applicationContext = context;
    }

    @Override
    public void addController(Object controllerAnnotated) {

    }

    @Override
    public void addFilter(WebFilter filter) {

    }

    @Override
    public void addInterceptor(WebInterceptor interceptor) {

    }

    @Override
    public void addWebSocketEndPoint(Object endPoint) {

    }

    private boolean checkHeaders(RequestDefine define, String[] headers) {
        for (String header : headers) {
            if (StrUtil.isBlank(define.header(header))) {
                LOGGER.warn("{} 缺少至少一个 header： {}", define.getUri(), header);
                return false;
            }
        }
        return true;
    }

    private boolean checkRequiredEntity(RequestDefine define, Iterable<Class<?>> clsRequired) {
        Map<String, List<String>> parameters = define.getParameters();
        for (Class<?> aClass : clsRequired) {
            if (applicationContext.contain(aClass)) {
                continue;
            }
            if (!containAtLeast(aClass, define.getParameters().keySet())) {
                return false;
            }

        }
        return true;
    }

    private boolean containAtLeast(Class<?> cls, Collection<String> paramsName) {
        try {
            Field[] declaredFields = cls.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                String name = declaredField.getName();
                if (PrimitiveTypeUtil.isPriType(declaredField.getType()) && paramsName.contains(name)) {
                    /* 有一个属性的名称就算 */
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 直接根据url获取
     *
     * @param requestUrl
     * @return
     */
    private ControllerDefine fastGetController(String requestUrl) {
        ControllerDefine controller = controllerMap.get(requestUrl);
        if (controller != null) {
            return controller;
        }
        String path = null;
        try {
            path = URLUtil.getPath(requestUrl);
        } catch (Exception e) {
            return null;
        }
        return controllerMap.get(path);
    }

    @Override
    public ControllerDefine getController(RequestDefine define) {
        ControllerDefine fastResult = fastGetController(define.getUri());
        if (fastResult != null) {
            if (!(fastResult.getMethodType().equals(RequestMethod.ANY)
                    | fastResult.getMethodType().equals(define.getMethodType()))) {
                LOGGER.warn("{} 请求方法不匹配", define.getUri());
                return null;
            }
            /* header匹配 */
            Mapping mapping = fastResult.getMapping();
            if (ArrayUtil.isNotEmpty(mapping.header())) {
                String[] headers = mapping.header();
                if (!checkHeaders(define, headers)) {
                    return null;
                }
            }
            /* 参数匹配 */
            Set<String> list = fastResult.requiredParameters();
            if (CollectionUtil.isNotEmpty(list)) {
                if (!checkNamedParam(list)) {
                    return null;
                }
            }
            return fastResult;
            // else一个一个匹配
        } else {
            return slowGetController(define);
        }

    }

    @Override
    public Engine getEngine() {
        return engine;
    }

    @Override
    public List<WebFilter> getFilters(String url) {
        if (this.filterList == null) {
            filterList = applicationContext.getBeanAssignableFromClass(WebFilter.class);

        }
        if (CollectionUtil.isEmpty(filterList)) {
            return null;
        }
        List<WebFilter> result = new ArrayList<>();
        for (WebFilter webFilter : filterList) {
            WebAppFilter appFilter = webFilter.getClass().getAnnotation(WebAppFilter.class);
            if (appFilter == null) {
//                result.add(webFilter);
                continue;
            }
            String[] matchs = appFilter.value();
            if (ArrayUtil.isEmpty(matchs)) {
                result.add(webFilter);
                continue;
            }
            matchCy:
            for (String match : matchs) {
                if (StrUtil.isBlank(match)) {
                    result.add(webFilter);
                    break matchCy;
                }
                if (PathUtil.pathMatch(url, match)) {
                    result.add(webFilter);
                    break matchCy;
                }
            }
        }
        return result;
    }

    @Override
    public List<WebInterceptor> getInterceptors(String url) {
        if (this.interceptorList == null) {
            this.interceptorList = applicationContext.getBeanAssignableFromClass(WebInterceptor.class);
        }
        if (CollectionUtil.isEmpty(filterList)) {
            return null;
        }
        List<WebInterceptor> result = new ArrayList<>();
        for (WebInterceptor interceptor : interceptorList) {
            WebAppInterceptor appInterceptor = interceptor.getClass().getAnnotation(WebAppInterceptor.class);
            if (appInterceptor == null) {
                continue;
            }
            String[] matchs = appInterceptor.value();
            if (ArrayUtil.isEmpty(matchs)) {
                result.add(interceptor);
                continue;
            }
            matchCy:
            for (String match : matchs) {
                if (StrUtil.isBlank(match)) {
                    result.add(interceptor);
                    break matchCy;
                }
                if ("**".equals(match)) {
                    result.add(interceptor);
                    break matchCy;
                }
                if (PathUtil.pathMatch(url, match)) {
                    result.add(interceptor);
                    break matchCy;
                }
            }

        }
        return result;
    }

    /**
     * 获得方法调用的参数
     *
     * @param method         方法
     * @param parameterTypes 方法参数类型
     * @return 参数
     * @throws Exception 参数异常
     */
    private Object[] getParameters(Method method, Class<?>[] parameterTypes) throws Exception {
        // 用于存放调用参数的对象数组
        Object[] parameters = new Object[parameterTypes.length];

        // 获得所调用方法的参数的Annotation数组
        Annotation[][] annotationArray = method.getParameterAnnotations();

        Map<String, List<String>> paramMap = TemporaryDataHolder.loadLemonRequest().parameterMap();

        // 构造调用所需要的参数数组
        for (int i = 0; i < parameterTypes.length; i++) {
            Object parameter;
            Class<?> type = parameterTypes[i];
            Annotation[] annotation = annotationArray[i];
            // 如果该参数没有RouterParam注解
            if (annotation == null || annotation.length == 0) {
                // 如果该参数类型是基础类型，则需要加RouterParam注解
                if (PrimitiveTypeUtil.isPriType(type)) {
                    LOGGER.warn("Must specify a @Param annotation for primitive type parameter in method={}",
                            method.getName());
                    continue;
                }
                if (Map.class.isAssignableFrom(type)) {
                    if (isModelMap(method, i)) {
                        parameters[i] = TemporaryDataHolder.loadModelMap();
                        continue;
                    }
                    LOGGER.warn("使用modeMap必须定义泛型参数");
                    parameters[i] = new HashMap<>();
                    continue;
                }
                /**
                 * httpRequest
                 */
                if (HttpRequest.class.isAssignableFrom(type)) {
                    parameters[i] = TemporaryDataHolder.loadHttpRequest();
                    continue;
                }
                if (cn.geeklemon.server.request.HttpRequest.class.isAssignableFrom(type)) {
                    parameters[i] = TemporaryDataHolder.loadLemonRequest();
                    continue;
                }
                if (HttpResponse.class.isAssignableFrom(type)) {
                    parameters[i] = TemporaryDataHolder.loadLemonResponse();
                    continue;
                }
                if (ModelAndView.class.isAssignableFrom(type)) {
                    parameters[i] = TemporaryDataHolder.loadModelAndView();
                    continue;
                }
                // 封装对象类型的parameter
                parameter = type.newInstance();
                // 将map中的属性映射到bean中,由于不是基础类型,bean中有各种属性，可以这样使用，
                // 需要增加如果注入某些属性例如 httpRequest，或者其他的可以注入的类
                // 如果是基础类型却没有注解，，就无法绑定参数，那么上面的continue跳出本次操作
                BeanUtils.populate(parameter, paramMap);
                parameters[i] = parameter;
            } else if (annotation[0] instanceof MultiFile) {
                /*
                 * 绑定file参数
                 */
                MultiFile multiFile = (MultiFile) annotation[0];
                LOGGER.debug("进入multipart");
                parameters[i] = MultiFileUtil.getMultiFileObj(TemporaryDataHolder.loadFullHttpRequest(), multiFile);
            } else if (annotation[0] instanceof Param) {
                Param param = (Param) annotation[0];
                try {
                    // 生成当前的调用参数
                    parameter = parseParameter(paramMap, type, param, method, i);
                    // if (param.notNull()) {
                    // Assert.notNull(parameter);
                    // }
                    // if (param.notBlank()) {
                    // Assert.notBlank((String) parameter, "{} 不能为空！",
                    // param.key());
                    // }
                    parameters[i] = parameter;
                } catch (Exception e) {
                    LOGGER.error("param [" + param.key() + "] is invalid，cause:" + e.getMessage());
                    throw new IllegalArgumentException("参数 " + param.key() + " 不合法：" + e.getMessage());
                }
            } else {
                parameters[i] = applicationContext.getBean(type);
            }
        }
        return parameters;
    }

    @Override
    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    @Override
    public ViewEngineConfig getViewEngineConfig() {
        return viewEngineConfig;
    }

    @Override
    public WebSocketContext getWebSocketContext() {
        return webSocketContext;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ModelAndView invokForModelAndView(ControllerDefine define, cn.geeklemon.server.request.HttpRequest request)
            throws Exception {
        Method method = define.getMethod();
        Class<?> returnType = method.getReturnType();

        Class<?> aClass = method.getDeclaringClass();
        Object bean = controllerCache.get(aClass);
        if (bean == null) {
            /*applicationContext的getBean方法需要检查类型匹配，速度较慢*/
            bean = applicationContext.getBean(aClass);
            if (bean != null) {
                controllerCache.put(aClass, bean);
            }
        }
        Object[] parameters = getParameters(method, method.getParameterTypes());
        Object result = null;
        if (ObjectUtil.isNull(bean)) {
            System.out.println("------------根据controller获取不到bean" + aClass);
        }
        result = ReflectUtils.invoke(bean, method, parameters);
        if (void.class.isAssignableFrom(returnType)) {
            return null;
        }
        if (request instanceof ModelAndView) {
            return (ModelAndView) result;
        }
        ModelAndView modelAndView = TemporaryDataHolder.loadModelAndView();
        RenderType renderType = define.getRenderType();
        modelAndView.setRenderType(renderType);
        if (renderType == RenderType.HTML || renderType == RenderType.VIEW) {
            ViewEngineConfig viewEngineConfig = getViewEngineConfig();
            String prefix = viewEngineConfig.getPrefix();
            String suffix = viewEngineConfig.getSuffix();
            if (result instanceof ModelAndView) {
                ModelAndView mView = (ModelAndView) result;
                mView.setRenderType(renderType);
                String name = mView.getTemplateName();
                String fileName = prefix + name + suffix;
                mView.setTemplateName(fileName);
                return mView;
            }
            String string = result.toString();
            String fileName = prefix + string + suffix;

            Map<String, Object> modelMap = TemporaryDataHolder.loadModelMap();
            /**
             * 用户可能直接使用ModelMap
             */
            modelAndView.setModel(modelMap);
            modelAndView.setTemplateName(fileName);
            return modelAndView;
        }

        modelAndView.setContent(result);
        return modelAndView;
    }

    /**
     * 检查map的key泛型参数是否是String
     */

    private boolean isModelMap(Method method, int index) {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (index >= genericParameterTypes.length || index < 0) {
            return false;
        }
        Type genericParameterType = genericParameterTypes[index];
        if (genericParameterType instanceof ParameterizedType) {
            try {
                ParameterizedType aType = (ParameterizedType) genericParameterType;
                Type[] parameterArgTypes = aType.getActualTypeArguments();

                Type type1 = parameterArgTypes[0];
                Type type2 = parameterArgTypes[1];
                Class<?> key = (Class<?>) type1;
                if (String.class.isAssignableFrom(key)) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }

        }
        /* 说明没有定义泛型类型，直接使用了Map */
        return false;
    }

    @Override
    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    public void setViewEngineConfig(ViewEngineConfig config) {
        this.viewEngineConfig = config;
    }

    public void setWebSocketContext(WebSocketContext webSocketContext) {
        this.webSocketContext = webSocketContext;
    }

    private ControllerDefine slowGetController(RequestDefine define) {
        for (ControllerDefine controllerDefine : controllerSet) {
            String pathMatcher = controllerDefine.getPathMatcher();
            /* 请求类型 */
            if (!(RequestMethod.ANY.equals(controllerDefine.getMethodType())
                    | controllerDefine.getMethodType().equals(define.getMethodType()))) {
                continue;
            }
            /* 路径匹配 */
            if (!(PathUtil.addParamIfAccept(define.getPathMatcher(), controllerDefine.getPathMatcher()))) {
                continue;
            }

            /* header匹配 */
            Mapping mapping = controllerDefine.getMapping();
            if (ArrayUtil.isNotEmpty(mapping.header())) {
                String[] headers = mapping.header();
                if (!checkHeaders(define, headers)) {
                    continue;
                }
            }
            /* 参数匹配 */
            Set<String> list = controllerDefine.requiredParameters();
            if (CollectionUtil.isNotEmpty(list)) {
                if (!checkNamedParam(list)) {
                    continue;
                }
            }
            /**
             * 请求体匹配
             */
            // Set<Class<?>> classSet = controllerDefine.requiredEntity();
            // if (CollectionUtil.isNotEmpty(classSet)) {
            // if (!(checkRequiredEntity(define, classSet))) {
            // continue;
            // }
            // }
            return controllerDefine;
        }
        return null;
    }
}
