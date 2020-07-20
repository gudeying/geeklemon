package cn.geekelmon.data.support;

import java.lang.reflect.Method;
import java.util.List;

import cn.geekelmon.data.annotation.Transaction;
import cn.geeklemon.core.aop.AspectContext;
import cn.geeklemon.core.bean.factory.BeanDefinition;
import cn.geeklemon.core.bean.factory.PostProcessor;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.ClassUtil;

public class TransactionPostProcessor implements PostProcessor {
	private static JdbcExecutorFactory factory;

	@Override
	public Object process(Object bean, BeanDefinition beanDefinition) {
		Class<?> cls = beanDefinition.getSourceClass();

		List<Method> methods = ClassUtil.getPublicMethods(cls, new Filter<Method>() {

			@Override
			public boolean accept(Method method) {
				Transaction annotation = AnnotationUtil.getAnnotation(method, Transaction.class);
				return annotation != null;
			}
		});

		if (CollectionUtil.isEmpty(methods)) {
			return bean;
		}

		AspectContext.addVoidPoint(cls, new TransactionPoint(factory));

		return bean;
	}

	public static void setJdbcExecutorFactory(JdbcExecutorFactory factory) {
		TransactionPostProcessor.factory = factory;
	}
}
