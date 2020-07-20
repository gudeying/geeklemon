package cn.geeklemon.core.util;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AnnotationUtils {

    /**
     * 判断一个类是否有某个注解，会根据组合注解查找
     *
     * @param target 目标类
     * @param search 要验证的注解
     * @return
     */
    public static boolean contain(Class<?> target, Class<? extends Annotation> search) {
        try {
            Annotation annotation = AnnotationUtil.getAnnotation(target, search);
            return ObjectUtil.isNotNull(annotation);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 找到类上的注解，包括组合注解，未找到返回空
     *
     * @param clazz
     * @param annotationType
     * @param <A>
     * @return
     */
    static <A extends Annotation> A findAnnotationOnClass(Class<?> clazz, Class<A> annotationType) {
        return AnnotationUtil.getAnnotation(clazz, annotationType);
    }

    public static Set<Annotation> getAnnotationSet(Class<?> clazz, Class<? extends Annotation> annotationType) {
        Set<Annotation> annotations = new HashSet<>();
        getAnnotations(clazz, annotationType, annotations);
        return annotations;
    }

    private static void getAnnotations(Class<?> clazz, Class<? extends Annotation> annotationType, Set<Annotation> annotationSet) {
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println(annotation);
            if (!(isFromMetaAnnotations(annotation))) {
                System.out.println(annotation);
                getAnnotations(clazz, annotation.annotationType(), annotationSet);
            }
            annotationSet.add(annotation);
        }
    }

    public static boolean isFromMetaAnnotations(Annotation annotation) {
        return (annotation != null && annotation.annotationType().getName().startsWith("Ljava.lang.annotation"));
    }

    public static Annotation getAnnotationOnMethodOrInterFace(Method method, Class<? extends Annotation> annotationType) {
        try {
            Annotation annotation = method.getAnnotation(annotationType);
            if (annotation != null && annotation.annotationType().equals(annotationType)) {
                return annotation;
            }
            return getMethodAnnotationOnInterface(method, annotationType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Annotation getMethodAnnotationOnInterface(Method method, Class<? extends Annotation> annotationType) {
        if (ObjectUtil.isNotNull(method) && ObjectUtil.isNotNull(annotationType)) {
            Class cls = method.getDeclaringClass();
            Class[] interfaces = cls.getInterfaces();
            for (Class anInterface : interfaces) {
                try {
                    Method interfaceMethod = anInterface.getMethod(method.getName(), method.getParameterTypes());
                    Annotation annotation = interfaceMethod.getAnnotation(annotationType);
                    if (annotation.annotationType().equals(annotationType)) {
                        return annotation;
                    }
                } catch (Exception ignored) {

                }
            }
        }
        return null;
    }

    public static Set<Annotation> getMethodAnnotationSetOnInterface(Method method) {
        Set<Annotation> set = new HashSet<>();
        Class cls = method.getDeclaringClass();
        Class[] interfaces = cls.getInterfaces();
        for (Class anInterface : interfaces) {
            try {
                Method interfaceMethod = anInterface.getMethod(method.getName(), method.getParameterTypes());
                Annotation[] annotations = interfaceMethod.getAnnotations();
                set.addAll(Arrays.asList(annotations));
            } catch (Exception ignored) {
            }
        }
        return set;
    }
}
