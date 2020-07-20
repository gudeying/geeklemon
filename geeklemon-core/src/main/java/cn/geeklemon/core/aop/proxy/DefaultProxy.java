package cn.geeklemon.core.aop.proxy;

import cn.geeklemon.core.aop.AopMethodDefine;
import cn.geeklemon.core.aop.AopType;
import cn.geeklemon.core.aop.PointDefine;
import cn.geeklemon.core.aop.ProxyDefine;
import cn.geeklemon.core.context.support.LemonContext;
import cn.geeklemon.core.context.support.MyComparator;
import cn.geeklemon.core.util.AnnotationUtils;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class DefaultProxy implements MethodInterceptor {

    private List<ProxyDefine> before = new ArrayList<>();
    private List<ProxyDefine> after = new ArrayList<>();
    private List<ProxyDefine> around = new ArrayList<>();
    private LemonContext context = LemonContext.getInstance();

    private List<ProxyDefine> proxyDefines;


    private Set<AopMethodDefine> annotatedMethods;
    private Object target;


    public DefaultProxy(Set<AopMethodDefine> annotatedMethods, List<ProxyDefine> proxyDefines, Object bean) {
        this.proxyDefines = proxyDefines;
        this.annotatedMethods = annotatedMethods;
        this.target = bean;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        /**
         * 根据method获取annotation，根据annotation获得切面
         */
        //所有的aop切面类信息
        Set<Class<? extends Annotation>> annotations = getAopAnnotations(method);
        Throwable exception = null;
//        System.out.println(method);
//        System.out.println(args);
//        if (1 == 1) {
//            return null;
//        }
//        System.out.println();
        if (annotations.isEmpty()) {
            return ReflectUtil.invoke(target, method, args);
        }
        /**
         *分类
         */
        divide(proxyDefines);
        /*排序*/
        PointDefine pointDefine = new PointDefine(obj, method, args, null);
        /*执行*/
        doProcess(pointDefine, before, annotations, method);
        doProcess(pointDefine, around, annotations, method);
        Object result = null;
        try {
            result = ReflectUtil.invoke(target, method, args);
            pointDefine.setResult(result);
        } catch (Exception e) {
            exception = e;
        } finally {
            doProcess(pointDefine, around, annotations, method);
            doProcess(pointDefine, after, annotations, method);
            if (exception != null) {
                throw exception;
            }
        }
        return result;
    }

    private void doProcess(PointDefine pointDefine, List<ProxyDefine> around, Set<Class<? extends Annotation>> annotations, Method method) throws InstantiationException, IllegalAccessException {
        if (!around.isEmpty()) {
            for (Class<? extends Annotation> annotation : annotations) {
                for (ProxyDefine proxyDefine : around) {
                    if (annotation == proxyDefine.getAnnotation()) {

                        Object object = context.getBean(proxyDefine.getTarget());
                        if (ObjectUtil.isNull(object)) {
                            object = proxyDefine.getTarget().newInstance();
                        }
                        String methodName = proxyDefine.getMethodName();
                        Annotation ann = AnnotationUtils.getAnnotationOnMethodOrInterFace(method, annotation);
                        pointDefine.setAnnotation(ann);
//                        System.out.println("--------【代理】");
//                        System.out.println(object);
//                        System.out.println(methodName);
//                        System.out.println(pointDefine);
//                        System.out.println("--------【代理】");
                        ReflectUtil.invoke(object, methodName, pointDefine);
                    }
                }
            }

        }
    }

    private Set<Class<? extends Annotation>> getAopAnnotations(Method method) {
        Set<Class<? extends Annotation>> classes = new HashSet<>();
        for (AopMethodDefine annotatedMethod : annotatedMethods) {
            if (annotatedMethod.getMethod().equals(method)) {
                classes.add(annotatedMethod.getAnnotation());
            }
        }
        return classes;
    }

    private void divide(List<ProxyDefine> list) {
        for (ProxyDefine proxyDefine : list) {
            AopType aopType = proxyDefine.getAopType();
            switch (aopType) {
                case BEFORE:
                    before.add(proxyDefine);
                    break;
                case AFTER:
                    after.add(proxyDefine);
                    break;
                case AROUND:
                    around.add(proxyDefine);
                    break;
            }
        }
        MyComparator myComparator = new MyComparator();
        before.sort(myComparator);
        after.sort(myComparator);
        around.sort(myComparator);

    }

    private boolean accept(Method method) {
        for (AopMethodDefine annotatedMethod : annotatedMethods) {
            if (method == annotatedMethod.getMethod()) {
                return true;
            }
        }
        return false;
    }
}
