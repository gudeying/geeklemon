package cn.geeklemon.core.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/18 17:02
 * Modified by : kavingu
 */
public class AopMethodDefine {
    /**
     * 需要aop的方法上的注解
     */
    private Class<? extends Annotation> annotation;
    /**
     * 需要aop的方法
     */
    private Method method;

    public AopMethodDefine(Class<? extends Annotation> annotation, Method method) {
        this.annotation = annotation;
        this.method = method;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AopMethodDefine that = (AopMethodDefine) o;

        if (annotation != null ? !annotation.equals(that.annotation) : that.annotation != null) return false;
        return method != null ? method.equals(that.method) : that.method == null;
    }

    @Override
    public int hashCode() {
        int result = annotation != null ? annotation.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
