package cn.geeklemon.core.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/19 9:33
 * Modified by : kavingu
 */
public class PointDefine {
    private Object target;
    private Member method;
    private Object[] args;
    private Object result;
    private Annotation annotation;
    private Throwable exception;


    public PointDefine(Object target, Member method, Object[] args, Object result) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.result = result;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Member getMethod() {
        return method;
    }

    public void setMethod(Member method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getException() {
        return exception;
    }

    public PointDefine setException(Throwable exception) {
        this.exception = exception;
        return this;
    }
}
