package cn.geeklemon.core.bean.factory;

import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.context.support.DefaultFactoryBean;
import cn.geeklemon.core.context.support.chain.FactoryBean;
import cn.geeklemon.core.util.BeanNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class BeanInitializationDefinition {
	/**
	 * 产生这个bean的具体实例类
	 */
	private Class<?> target;
	/**
	 * bean的实际类型
	 */
	private Class<?> srcClass;
	private InitType initType;
	private String beanName;
	private int order;
	private Method initMethod;
	private String targetBeanName;
	private boolean single = true;
	private boolean initOnlyOnUse;
	private Set<String> dependBeans = new HashSet<>();

	private FactoryBean factoryBean;

	private Object instance;

	public BeanInitializationDefinition(String beanName, Class<?> aClass) {
		this.beanName = beanName;
		this.target = aClass;
		this.srcClass = aClass;
		this.factoryBean = new DefaultFactoryBean(aClass, aClass);
		setDependBeans(searchBeanDepends());
	}

	public BeanInitializationDefinition() {
	}

	public BeanInitializationDefinition(Class<?> target) {
		this.target = target;
		this.initType = InitType.CONSTRUCT;
		this.beanName = BeanNameUtil.getBeanName(target);
		setDependBeans(searchBeanDepends());
	}

	public BeanInitializationDefinition(String name, Class<?> target, Class<?> srcClass) {
		this.beanName = name;
		this.target = target;
		this.srcClass = srcClass;
		this.initType = InitType.CONSTRUCT;
		this.factoryBean = new DefaultFactoryBean(srcClass, target);
		setDependBeans(searchBeanDepends());
	}

	public BeanInitializationDefinition(String name, FactoryBean factoryBean) {
		this.beanName = name;
		this.srcClass = factoryBean.getObjectType();
		this.initType = InitType.CONSTRUCT;
		this.factoryBean = factoryBean;
		setDependBeans(searchBeanDepends());
	}

	public BeanInitializationDefinition(String name, Class<?> target, Class<?> srcClass, Method initMethod) {
		Assert.notBlank(name);
		this.beanName = name;
		this.target = target;
		this.srcClass = srcClass;
		this.initMethod = initMethod;
		this.targetBeanName = BeanNameUtil.getBeanName(target);
		this.initType = InitType.NON_PARAM_METHOD;
		setDependBeans(searchBeanDepends());
	}

	// public Object getBeanInstance() {
	// if (target.isInterface()) {
	// return null;
	// }
	// if (this.instance != null) {
	// return this.instance;
	// }
	// Object targetInstance = null;
	// try {
	// targetInstance = target.newInstance();
	// } catch (InstantiationException | IllegalAccessException e) {
	// e.printStackTrace();
	// return null;
	// }
	// if (FactoryBean.class.isAssignableFrom(target) && targetInstance != null)
	// {
	// return ReflectUtil.invoke(targetInstance, "getBeanInstance");
	// }
	// return targetInstance;
	// }
	public Object getBeanInstance() {
		try {
			return this.factoryBean.getObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public InitType getInitType() {
		return initType;
	}

	public void setInitType(InitType initType) {
		this.initType = initType;
	}

	public Method getInitMethod() {
		return initMethod;
	}

	public void setInitMethod(Method initMethod) {
		this.initMethod = initMethod;
		this.initType = InitType.NON_PARAM_METHOD;
	}

	// public Class<?> getTarget() {
	//
	// return target;
	// }

	public void setTarget(Class<?> target) {
		this.target = target;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Set<String> getDependBeans() {
		return dependBeans;
	}

	private void setDependBeans(Set<String> dependBeans) {
		this.dependBeans = dependBeans;
	}

	private Set<String> searchBeanDepends() {
		Set<String> classSet = new HashSet<>();
		if (ObjectUtil.isNull(this.target)) {
			return classSet;
		}
		/* 属性中@Autowired依赖 */
		Field[] fields = target.getDeclaredFields();
		if (fields != null) {
			for (Field field : fields) {
				Autowired autowired = field.getAnnotation(Autowired.class);
				Class<?> type = field.getType();
				/**
				 * 除了了applicationContext，这个在最先注册
				 */
				if (autowired != null && (!ApplicationContext.class.isAssignableFrom(type))) {
					String name;
					String autoName = autowired.name();
					if (StrUtil.isNotBlank(autoName)) {
						name = autoName;
					} else {
						name = type.getName();
					}
					classSet.add(name);
				}
			}
		}
		/**
		 * setter方法上的依赖
		 */
		PropertyDescriptor[] descriptors;
		try {
			descriptors = Introspector.getBeanInfo(target).getPropertyDescriptors();
		} catch (IntrospectionException e) {
			return classSet;
		}
		if (ArrayUtil.isEmpty(descriptors))
			return classSet;
		for (PropertyDescriptor descriptor : descriptors) {
			// 获取所有set方法
			Method setter = descriptor.getWriteMethod();
			// 判断set方法是否定义了注解
			if (setter != null && setter.isAnnotationPresent(Autowired.class) && setter.getParameterCount() == 1) {
				/* setter方法应该只能有一个参数 */
				Autowired autowired = setter.getAnnotation(Autowired.class);
				final Class<?>[] parameterTypes = setter.getParameterTypes();
				Class<?> type = parameterTypes[0];
				classSet.add(type.getName());
			}
		}
		return classSet;
	}

	public String getTargetBeanName() {
		return targetBeanName;
	}

	public void setTargetBeanName(String targetBeanName) {
		this.targetBeanName = targetBeanName;
	}

	public boolean isSingle() {
		return single;
	}

	public void setSingle(boolean single) {
		this.single = single;
	}

	public boolean isInitOnlyOnUse() {
		return initOnlyOnUse;
	}

	public void setInitOnlyOnUse(boolean initOnlyOnUse) {
		this.initOnlyOnUse = initOnlyOnUse;
	}

	public Class<?> getSrcClass() {
		return srcClass;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	public Class<?> getTarget() {
		return target;
	}
}
