package cn.geeklemon.core.aop;

import java.lang.annotation.Annotation;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/18 11:46
 * Modified by : kavingu
 */
public class ProxyDefine {
    /**
     * 切点类
     */
    private Class<?> target;
    /**
     * 切点方法名称，参数是统一的，所以不需要方法参数列表
     */
    private String methodName;
    private AopType aopType;
    /**
     * 该方要法切入有这个注解的方法
     */
    private Class<? extends Annotation> annotation;

    public ProxyDefine(Class<?> target, String methodName, AopType aopType, Class<? extends Annotation> annotation) {
        this.target = target;
        this.methodName = methodName;
        this.aopType = aopType;
        this.annotation = annotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProxyDefine that = (ProxyDefine) o;

        if (!target.equals(that.target)) return false;
        if (!methodName.equals(that.methodName)) return false;
        if (aopType != that.aopType) return false;
        return annotation.equals(that.annotation);
    }

    @Override
    public int hashCode() {
        int result = target.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + aopType.hashCode();
        result = 31 * result + annotation.hashCode();
        return result;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public AopType getAopType() {
        return aopType;
    }

    public void setAopType(AopType aopType) {
        this.aopType = aopType;
    }
}
