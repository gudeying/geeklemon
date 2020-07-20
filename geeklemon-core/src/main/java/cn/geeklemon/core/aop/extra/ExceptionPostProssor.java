package cn.geeklemon.core.aop.extra;

import java.lang.reflect.Method;

import cn.geeklemon.core.aop.AspectContext;
import cn.geeklemon.core.bean.factory.BeanDefinition;
import cn.geeklemon.core.bean.factory.PostProcessor;

public class ExceptionPostProssor implements PostProcessor {

	@Override
	public Object process(Object bean, BeanDefinition beanDefinition) {
		try {
			Class<?> sourceClass = beanDefinition.getSourceClass();
			Method[] methods = sourceClass.getDeclaredMethods();
			for (Method method : methods) {
				ExceptionAvoid avoid = method.getAnnotation(ExceptionAvoid.class);
				if (avoid != null) {
					AspectContext.addVoidPoint(sourceClass, new ExceptionHandlerPoint());
				}
			}
		} catch (SecurityException e) {
		}
		return bean;
	}

}
