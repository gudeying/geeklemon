package cn.geeklemon.core.aop.extra;

import java.lang.reflect.Method;

import cn.geeklemon.core.util.PrimitiveTypeUtil;
import cn.hutool.core.util.ClassUtil;

/**
 * 除了基础数据返回默认值，其他返回null
 * 
 * @author Goldin
 *
 */
public class NullDefaultExceptionHandler implements ExceptionHandler {

	@Override
	public Object handle(ExceptionPoint exceptionPoint) {

		Method method = exceptionPoint.getMethod();
		if (method != null) {
			Class<?> returnType = method.getReturnType();
			return getNullDefaultValue(returnType);
		} else {
			return null;
		}
	}

	private Object getNullDefaultValue(Class<?> returnType) {
		if (PrimitiveTypeUtil.isPriType(returnType)) {
			if (ClassUtil.isPrimitiveWrapper(returnType)) {
				return null;
			} else {
				return PrimitiveTypeUtil.getPriDefaultValue(returnType);
			}
		}
		return null;
	}
}
