package cn.geeklemon.server.context;

import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.util.PrimitiveTypeUtil;
import cn.geeklemon.server.TemporaryDataHolder;
import cn.geeklemon.server.common.PathUtil;
import cn.geeklemon.server.common.RequestMethod;
import cn.geeklemon.server.controller.ControllerDefine;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.controller.annotation.Param;
import cn.geeklemon.server.multipart.MultiFileUtil;
import cn.geeklemon.server.multipart.annotation.MultiFile;
import cn.geeklemon.server.request.RequestDefine;
import cn.geeklemon.server.response.HttpResponse;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
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

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/23 9:10
 * Modified by : kavingu
 */
public class LemonServerWebContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(LemonServerWebContext.class);


    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        LemonServerWebContext.applicationContext = applicationContext;
    }

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

    public static boolean containParamRegular(Class<?> cls) {
        return requestParamAssigned.contains(cls);
    }
}
