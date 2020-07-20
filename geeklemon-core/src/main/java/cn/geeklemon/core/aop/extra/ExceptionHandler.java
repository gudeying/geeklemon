package cn.geeklemon.core.aop.extra;

/**
 * 异常发生时拦截调用的方法
 * 
 * @author Goldin
 *
 */
public interface ExceptionHandler {
	/**
	 * 
	 * @param exceptionPoint
	 * @return 发生异常时方法最终需要返回的结果
	 */
	Object handle(ExceptionPoint exceptionPoint);

}
