package cn.geeklemon.core.context.support;

import cn.geeklemon.core.bean.factory.BeanScannerFilter;

import java.util.List;

public interface ApplicationContext {

	/**
	 * 第一个符合type的bean
	 * 
	 * @param cls
	 * @return
	 */
	<T> T getBean(Class<T> cls);

	/**
	 * beanName必定不能重复
	 */
	Object getBean(String beanName);

	/**
	 * 直接注册单例bean
	 *
	 */
	void register(Class<?> cls) throws Exception;


	/**
	 * 
	 * @param cls
	 * @return list 可能为empty，但是不会为null
	 */
	<T> List<T> getBeanAssignableFromClass(Class<T> cls);

	boolean contain(Class<?> entity);

}
