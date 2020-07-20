package cn.geekelmon.app.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geekelmon.app.api.entity.ApiEntity;
import cn.geeklemon.core.aop.extra.ExceptionHandler;
import cn.geeklemon.core.aop.extra.ExceptionPoint;

/**
 * 统一处理controller的异常
 * 
 * @author Goldin
 *
 */
public class ControllerExceptionHandler implements ExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

	@Override
	public Object handle(ExceptionPoint exceptionPoint) {
		if (exceptionPoint == null) {
			//应该不会出现这种情况
			return new ApiEntity<>("请求出错");
		}
		Throwable throwable = exceptionPoint.getException();
		try {
			LOGGER.error("{}请求出错:{}", exceptionPoint.getMethod().getName(), exceptionPoint.getException());

			if (throwable instanceof ControllerException) {
				String message = exceptionPoint.getException().getMessage();
				return new ApiEntity<>(message);
			}
			if (throwable instanceof NumberFormatException) {
				return new ApiEntity<>("参数格式不正确");
			}
			return new ApiEntity<>("请求出错");
		} catch (Exception e) {
			// 防止空指针等错误
			return new ApiEntity<>("请求出错");
		}
	}

}
