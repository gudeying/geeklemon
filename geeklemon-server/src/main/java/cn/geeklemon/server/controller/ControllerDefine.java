package cn.geeklemon.server.controller;

import cn.geeklemon.server.common.RenderType;
import cn.geeklemon.server.common.RequestMethod;
import cn.geeklemon.server.context.LemonServerWebContext;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.controller.annotation.Param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/23 9:11
 * Modified by : kavingu
 */
public class ControllerDefine {
    private String pathMatcher;
    private Method method;
    private RequestMethod methodType;
    private RenderType renderType;
    private Mapping mapping;

    private Set<String> parameters = new HashSet<>();

    private Set<Class<?>> requiredEntity = new HashSet<>();


    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public String toString() {
        return "ControllerDefine{" +
                "pathMatcher='" + pathMatcher + '\'' +
                ", method=" + method +
                ", methodType=" + methodType +
                ", renderType=" + renderType +
                '}';
    }

    public ControllerDefine(Mapping mapping, Method method) {
        this.pathMatcher = mapping.path();
        this.method = method;
        this.methodType = mapping.requestMethod();
        this.renderType = mapping.renderType();
        this.mapping = mapping;
        /*使用@Param注解需要的参数*/
        searchParameterRequired();
        /*直接populate为一个类的参数*/
        searchRequiredEntityClass();
    }

    /**
     * 该方法使用@Param注解的需要的参数
     */
    private void searchParameterRequired() {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] annotation = parameterAnnotations[i];
            if (annotation == null || annotation.length == 0) {
                continue;
            }
            if (annotation[0] instanceof Param) {
                Param param = (Param) annotation[0];
                if (param.notNull()) {
                    parameters.add(param.key());
                }
            }
        }
    }

    private void searchRequiredEntityClass() {
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> parameterType : parameterTypes) {
            if (!LemonServerWebContext.containParamRegular(parameterType)) {
                continue;
            }
            requiredEntity.add(parameterType);
        }
    }


    public RenderType getRenderType() {
        return renderType;
    }

    public void setRenderType(RenderType renderType) {
        this.renderType = renderType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ControllerDefine that = (ControllerDefine) o;

        if (pathMatcher != null ? !pathMatcher.equals(that.pathMatcher) : that.pathMatcher != null) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        return methodType != null ? methodType.equals(that.methodType) : that.methodType == null;
    }

    @Override
    public int hashCode() {
        int result = pathMatcher != null ? pathMatcher.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (methodType != null ? methodType.hashCode() : 0);
        return result;
    }


    public RequestMethod getMethodType() {
        return methodType;
    }

    public void setMethodType(RequestMethod methodType) {
        this.methodType = methodType;
    }

    public String getPathMatcher() {
        return pathMatcher;
    }

    public void setPathMatcher(String pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Set<String> requiredParameters() {
        return parameters;
    }

    public Set<Class<?>> requiredEntity() {
        return this.requiredEntity;
    }

}
