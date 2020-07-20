package cn.geeklemon.core.aop;

import cn.geeklemon.core.aop.support.LProxyFactory;
import cn.geeklemon.core.aop.support.VoidPoint;
import cn.geeklemon.core.bean.factory.BeanDefinition;
import cn.geeklemon.core.bean.factory.PostProcessor;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.hutool.core.collection.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Set;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2020/1/16 11:38 Modified by : kavingu
 */
public class LAopPostProcess implements PostProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(LAopPostProcess.class);
	private ApplicationContext context;

	public LAopPostProcess(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public Object process(Object bean, BeanDefinition beanDefinition) {
		Class<?> sourceClass = beanDefinition.getSourceClass();
		Set<VoidPoint> set = AspectContext.getClassVoidPoint(sourceClass);
		if (CollectionUtil.isEmpty(set)) {
			return bean;
		}
		LOGGER.info("【AOP】 {}", beanDefinition.getSourceClass());
		return LProxyFactory.create().getProxy(bean, new LinkedList<>(set), context);
	}
}
