package cn.geeklemon.core.bean.factory;

import cn.geeklemon.core.util.BeanNameUtil;

public class BeanDefinition {
	private Class<?> sourceClass;
	private String name;
	private Object instance;

	public BeanDefinition(Object instance) {
		this.instance = instance;
		this.sourceClass = instance.getClass();
		this.name = BeanNameUtil.getBeanName(sourceClass);
	}

	public BeanDefinition(String name, Object instance) {
		this.name = name;
		this.instance = instance;
		this.sourceClass = instance.getClass();
	}

	public BeanDefinition(Class<?> sourceClass, String name, Object instance) {
		this.sourceClass = sourceClass;
		this.name = name;
		this.instance = instance;
	}

	public BeanDefinition() {
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public void setSourceClass(Class<?> sourceClass) {
		this.sourceClass = sourceClass;
	}

	/**
	 * 
	 * @return beanName
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取实例化好的bean
	 */
	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	@Override
	public int hashCode() {
		return (sourceClass.getName() + name).hashCode() + instance.hashCode();
	}

	/**
	 * 名称和类相同即为相等
	 *
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BeanDefinition))
			return false;
		BeanDefinition tar = (BeanDefinition) obj;
		return tar.getName().equals(this.name) && tar.sourceClass == this.sourceClass;
	}
}
