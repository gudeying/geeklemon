package cn.geeklemon.core.aop.extra;

import java.lang.reflect.Method;

public class ExceptionPoint {
	private Object object;
	private Method method;
	private Object[] args;
	private Throwable exception;

	public ExceptionPoint(Object object, Method method, Object[] args, Throwable exception) {
		super();
		this.object = object;
		this.method = method;
		this.args = args;
		this.exception = exception;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

}
