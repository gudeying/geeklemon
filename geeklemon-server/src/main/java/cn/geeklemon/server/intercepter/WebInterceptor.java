package cn.geeklemon.server.intercepter;

import cn.geeklemon.server.request.HttpRequest;
import cn.geeklemon.server.viewrender.ModelAndView;

/**
 * controller执行的拦截<br/>
 * 如果在controller方法中使用response直接回写结果，那么只会执行preHandle()
 * 
 * @author Goldin
 *
 */
public interface WebInterceptor {
	/**
	 * 之前 返回true，交给controller执行，返回false，拦截
	 */
	boolean preHandle(HttpRequest request) throws Exception;

	/**
	 * controller执行完
	 */
	void postHandle(HttpRequest request, ModelAndView modelAndView) throws Exception;

	/**
	 * 包装好response之后
	 */
	void afterCompletion(HttpRequest request, Exception exception) throws Exception;
}
