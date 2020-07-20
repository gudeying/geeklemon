package cn.geeklemon.core.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReflectUtil;

/**
 * 反射工具类
 * 
 * @author Goldin
 *
 */
public class ReflectUtils {
	/**
	 * 不抛出 InvocationTargetException，抛出TargetException
	 */
	public static Object invoke(Object obj, Method method, Object... args) throws Exception {
		try {
			return ReflectUtil.invoke(obj, method, args);
		} catch (Exception e) {
			if (e instanceof UtilException) {
				UtilException utilException = (UtilException) e;
				Throwable throwable = utilException.getCause();
				if (throwable instanceof InvocationTargetException) {
					InvocationTargetException exception = (InvocationTargetException) throwable;
					Exception exception2 = (Exception) exception.getTargetException();
					throw exception2;
				}
			}
			throw e;
		}
	}
}
