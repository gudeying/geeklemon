package cn.geeklemon.core.aop;

import cn.geeklemon.core.aop.support.VoidPoint;
import cn.geeklemon.core.util.AnnotationUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 */
public class AspectContext {
    private static AspectContext ourInstance = new AspectContext();

    private static final Map<Class<?>, Set<VoidPoint>> voidPointMap = new HashMap<>();


    private static final Map<String, Set<VoidPoint>> stringVoidPointMap = new HashMap<>();


    private static final Map<Class<? extends Annotation>, Set<VoidPoint>> mapPoint = new HashMap<>();


    private static final Set<Method> aspectMethodSet = new HashSet<>();
    private static final Set<Class<? extends Annotation>> aspectAnnotationSet = new HashSet<>();


    public static AspectContext getInstance() {
        return ourInstance;
    }

    private AspectContext() {
    }


    private static boolean hasAspect(Method method) {
        return false;
    }

    private boolean annotatedAspect(Method method) {
        if (aspectMethodSet.contains(method)) {
            return true;
        }
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> aClass = annotation.annotationType();
            if (aspectAnnotationSet.contains(aClass)) {
                return true;
            }
        }
        Set<Annotation> onInterface = AnnotationUtils.getMethodAnnotationSetOnInterface(method);
        for (Annotation annotation : onInterface) {
            Class<? extends Annotation> aClass = annotation.annotationType();
            if (aspectAnnotationSet.contains(aClass)) {
                return true;
            }
        }
        return false;
    }

    public static void addAspectAnnotation(Class<? extends Annotation> ann) {
        aspectAnnotationSet.add(ann);
    }

    public static void addPoint(Class<? extends Annotation> ann, VoidPoint point) {
        Set<VoidPoint> voidPoints = mapPoint.get(ann);
        if (ObjectUtil.isNull(voidPoints)) {
            voidPoints = new HashSet<>();
            mapPoint.put(ann, voidPoints);
            mapPoint.get(ann).add(point);
        } else {
            mapPoint.get(ann).add(point);
        }
    }

    public static Map<String, Set<VoidPoint>> getStringVoidPointMap() {
        return stringVoidPointMap;
    }


    public static Map<Class<?>, Set<VoidPoint>> getVoidPointMap() {
        return voidPointMap;
    }


    public static Map<Class<? extends Annotation>, Set<VoidPoint>> getMapPoint() {
        return mapPoint;
    }

    public static Set<Method> getAspectMethodSet() {
        return aspectMethodSet;
    }

    public static Set<Class<? extends Annotation>> getAspectAnnotationSet() {
        return aspectAnnotationSet;
    }

    public static void buildContext(Class<?> sourceClass) {

        Set<VoidPoint> set = processAnnotatedAspect(sourceClass);
        Set<VoidPoint> voidPoints = voidPointMap.get(sourceClass);
        if (ObjectUtil.isNotNull(voidPoints)) {
            voidPointMap.get(sourceClass).addAll(set);
        } else {
            voidPointMap.put(sourceClass, set);
        }

    }

    private static Set<VoidPoint> processAnnotatedAspect(Class<?> sourceClass) {
        Set<VoidPoint> set = new HashSet<>();
        Method[] declaredMethods = ClassUtil.getDeclaredMethods(sourceClass);
        for (Method method : declaredMethods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                Set<VoidPoint> voidPoints = mapPoint.get(annotation.annotationType());
                if (CollectionUtil.isNotEmpty(voidPoints)) {
                    set.addAll(voidPoints);
                }
            }
            Set<Annotation> methodAnnotationSetOnInterface = AnnotationUtils.getMethodAnnotationSetOnInterface(method);
            for (Annotation annotation : methodAnnotationSetOnInterface) {
                Set<VoidPoint> set1 = mapPoint.get(annotation.annotationType());
                if (CollectionUtil.isNotEmpty(set1)) {
                    set.addAll(set1);
                }
            }
        }
        return set;
    }

    public static Set<VoidPoint> getClassVoidPoint(Class<?> cls) {
        Set<VoidPoint> set = new HashSet<>();
        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            String string = cls.getName() + "." + method.getName() + "()";
            Set<VoidPoint> stringVoidPoint = getStringVoidPoint(string);
            if (CollectionUtil.isNotEmpty(stringVoidPoint)) {
                set.addAll(stringVoidPoint);
            }
        }


        Set<VoidPoint> set1 = voidPointMap.get(cls);
        if (CollectionUtil.isNotEmpty(set1)) {
            set.addAll(set1);
        }
        return set;


    }

    public static Set<VoidPoint> getStringVoidPoint(Method method) {
        Class<?> aClass = method.getDeclaringClass();
        String string = aClass.getName() + "." + method.getName() + "()";
        return getStringVoidPoint(string);
    }

    private static Set<VoidPoint> getStringVoidPoint(String string) {
        Set<VoidPoint> set = new HashSet<>();
        stringVoidPointMap.forEach((key, val) -> {
            String regex = key.trim().replace(" ", "\\s+").replace(".", "\\.").replace("*", "\\w+").replace("(", "\\(").replace(")", "\\)");
            if (ReUtil.isMatch(regex, string)) {
                set.addAll(val);
            }
        });
        return set;
    }


    public static void addVoidPoint(Class<?> cls, VoidPoint voidPoint) {
        Set<VoidPoint> set = voidPointMap.get(cls);
        if (ObjectUtil.isNull(set)) {
            set = new HashSet<>();
            set.add(voidPoint);
            voidPointMap.put(cls, set);
        } else {
            voidPointMap.get(cls).add(voidPoint);
        }
    }

    public static void addStringVoidPoint(String string, VoidPoint voidPoint) {
        Set<VoidPoint> set = stringVoidPointMap.get(string);
        if (ObjectUtil.isNull(set)) {
            set = new HashSet<>();
            set.add(voidPoint);
            stringVoidPointMap.put(string, set);
        } else {
            stringVoidPointMap.get(string).add(voidPoint);
        }
    }
}

