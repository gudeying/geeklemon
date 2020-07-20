package cn.geeklemon.core.aop;

import cn.geeklemon.core.aop.support.PointResult;
import cn.geeklemon.core.aop.support.ProxyChain;
import cn.geeklemon.core.aop.support.ProxyResult;
import cn.geeklemon.core.aop.support.VoidPoint;
import cn.geeklemon.core.util.AnnotationUtils;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 */
public class LAopProxyPoint implements VoidPoint {

    /**
     * 使用 aopService 注解的类实体
     */
    private Object filter;

    /**
     * 使用 aopPoint 注解的方法名称
     */
    private String methodName;

    private AopType aopType;

    private Class<? extends Annotation>[] annotations;

    public LAopProxyPoint(Object filter, String methodName, AopType aopType, Class<? extends Annotation>[] annotations) {
        this.filter = filter;
        this.methodName = methodName;
        this.aopType = aopType;
        this.annotations = annotations;
    }

    @Override
    public void proceed(Object o, Method method, Object[] args, ProxyChain chain, MethodProxy methodProxy, PointResult pointResult) throws Throwable {

        Annotation[] annotations = method.getAnnotations();

        if (!doProcess(annotations, method, o)) {
            chain.proceed(o, method, args, methodProxy, pointResult);
            return;
        }
        if (filter == null) {
            chain.proceed(o, method, args, methodProxy, pointResult);
            return;
        }
        PointDefine define = new PointDefine(o, method, args, pointResult);
        switch (aopType) {
            case BEFORE:
                ReflectUtil.invoke(filter, methodName, define);
                chain.proceed(o, method, args, methodProxy, pointResult);
                return;
            case AFTER:
                chain.proceed(o, method, args, methodProxy, pointResult);
                ReflectUtil.invoke(filter, methodName, define);
                return;
            case AROUND:
                ReflectUtil.invoke(filter, methodName, define);
                Object proceed = chain.proceed(o, method, args, methodProxy, pointResult);
                define.setResult(proceed);
                ReflectUtil.invoke(filter, methodName, define);
                return;
            case AFTER_EXCEPTION:
                ProxyResult proxyResult = chain.proceed(o, method, args, methodProxy, pointResult);
                if (pointResult != null && proxyResult.getException() instanceof InvocationTargetException) {
                    define.setException(proxyResult.getException());
                    ReflectUtil.invoke(filter, methodName, define);
                }
        }
    }

    private boolean doProcess(Annotation[] methodAnnotations, Method method, Object object) {
        if (ArrayUtil.isEmpty(annotations)) {
            return false;
        }
        if (ArrayUtil.isEmpty(methodAnnotations)) {
            return false;
        }

        for (Annotation ma : methodAnnotations) {
            for (Class<? extends Annotation> ann : annotations) {
                if (ann.equals(ma.annotationType())) {
                    return true;
                }
            }
        }

        for (Class<? extends Annotation> annotation : annotations) {
            Annotation on = AnnotationUtils.getMethodAnnotationOnInterface(method, annotation);
            if (ObjectUtil.isNotNull(on)) {
                return true;
            }
        }
        return false;
    }
}
