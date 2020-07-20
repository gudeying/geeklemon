package cn.geeklemon.core.context.support;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geeklemon.core.aop.AspectContext;
import cn.geeklemon.core.aop.LAopPostProcess;
import cn.geeklemon.core.aop.LAopProxyPoint;
import cn.geeklemon.core.aop.LStringAopProxyPoint;
import cn.geeklemon.core.aop.annotation.AopPoint;
import cn.geeklemon.core.aop.annotation.AopProxy;
import cn.geeklemon.core.aop.annotation.PointCut;
import cn.geeklemon.core.bean.factory.BeanDefinition;
import cn.geeklemon.core.bean.factory.BeanInitializationDefinition;
import cn.geeklemon.core.bean.factory.BeanScannerFilter;
import cn.geeklemon.core.bean.factory.ClassDefinition;
import cn.geeklemon.core.bean.factory.InitType;
import cn.geeklemon.core.bean.factory.InitializingBean;
import cn.geeklemon.core.bean.factory.PostProcessor;
import cn.geeklemon.core.context.Processor.FieldPostProcessor;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.core.context.annotation.Import;
import cn.geeklemon.core.context.annotation.Value;
import cn.geeklemon.core.context.support.external.BeanInitDefineRegister;
import cn.geeklemon.core.context.support.external.BeanInitRegisterExternal;
import cn.geeklemon.core.exception.BeanDuplicationException;
import cn.geeklemon.core.util.BeanNameUtil;
import cn.geeklemon.core.util.PropsUtil;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.ClassScaner;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

/**
 */
public class LemonContext implements ApplicationContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	private final Map<String, BeanInitializationDefinition> initializationDefinitionMap = new HashMap<>();

	private final Set<ClassDefinition> classDefinitionSet = new LinkedHashSet<>();
	private Set<Class<?>> aopPointClass = new LinkedHashSet<>();
	private Set<BeanInitRegisterExternal> initRegisterSet = new LinkedHashSet<>();

	private final List<String> initedBeanName = new LinkedList<>();

	private final Set<BeanDefinition> singleBeanSet = new ConcurrentHashSet<>();
	/**
	 * bean赋值的时候对字段进行操作，如果字段被赋值，该字段将不被处理
	 */
	private List<FieldPostProcessor> fieldProcessorList = new LinkedList<>();

	private LAopPostProcess aopPostProcess = new LAopPostProcess(this);
	/**
	 * 实例属性赋值完毕后执行
	 */
	private List<PostProcessor> processorList = new LinkedList<>();

	private final Set<Class<? extends Annotation>> aopAnnSet = new HashSet<>();

	private static LemonContext instance = new LemonContext();

	private BeanInitDefineRegister initDefineRegister;

	private LemonContext() {
	}

	public static LemonContext getInstance() {
		return instance;
	}

	public synchronized ApplicationContext init(String[] scanPackages) throws Exception {

		String banner = "                       __   .____                                 \n"
				+ "   ____   ____   ____ |  | _|    |    ____   _____   ____   ____  \n"
				+ "  / ___\\_/ __ \\_/ __ \\|  |/ /    |  _/ __ \\ /     \\ /  _ \\ /    \\ \n"
				+ " / /_/  >  ___/\\  ___/|    <|    |__\\  ___/|  Y Y  (  <_> )   |  \\\n"
				+ " \\___  / \\___  >\\___  >__|_ \\_______ \\___  >__|_|  /\\____/|___|  /\n"
				+ "/_____/      \\/     \\/     \\/       \\/   \\/      \\/            \\/ \n";
		System.out.println(banner);

		initDefineRegister = new DefaultBeanInitDefineRegister();

		scan(scanPackages, new DefaultBeanScnnerFilter());

		sortPostProcessor();

		LOGGER.info("[LemonContext] 扫描到class数量：{}", classDefinitionSet.size());

		scanBeanInitializingDefinition();
		for (BeanInitRegisterExternal register : initRegisterSet) {
			prepareBean(register);
			if (register instanceof InitializingBean) {
				InitializingBean initializingBean = (InitializingBean) register;
				initializingBean.afterPropsSet();
			}
			register.register();
		}

		LOGGER.info("[LemonContext] 初始化bean定义数量：{}", initializationDefinitionMap.keySet().size());
		scanAop();

		initializationDefinitionMap.forEach((name, value) -> {
			AspectContext.buildContext(value.getSrcClass());
		});

		LOGGER.info("[LemonContext] AOP 注解数量：{}", aopAnnSet.size());

		BeanDefinition appBean = new BeanDefinition(this);
		putSingleBean(appBean);

		// processorList.add(new LAopPostProcess());
		// System.out.println("容器中的定义：----------------");
		// Set<Map.Entry<String, BeanInitializationDefinition>> entries =
		// initializationDefinitionMap.entrySet();
		// for (Map.Entry<String, BeanInitializationDefinition> entry : entries)
		// {
		// System.out.println("name :》" + entry.getKey());
		// System.out.println("define:》" + entry.getValue().getSrcClass());
		// System.out.println("single:》" + entry.getValue().isSingle());
		// System.out.println("");
		// }
		// System.out.println("容器中的定义：---------------- end");
		LOGGER.info("[LemonContext] 开始注入bean。。");
		injectBean();

		LOGGER.info("[LemonContext] 容器初始化完毕！");

		// beanSet.clear();

		return this;
	}

	private void scanAop() {

		aopPointClass.forEach(cls -> {
			try {
				Object newInstance = cls.newInstance();
				prepareBean(newInstance);

				Method[] publicMethods = ClassUtil.getPublicMethods(cls);
				for (Method publicMethod : publicMethods) {
					AopPoint aopPoint = publicMethod.getAnnotation(AopPoint.class);
					if (ObjectUtil.isNotNull(aopPoint)) {
						LAopProxyPoint proxyPoint = new LAopProxyPoint(newInstance, publicMethod.getName(),
								aopPoint.type(), aopPoint.value());
						Class<? extends Annotation>[] value = aopPoint.value();
						for (Class<? extends Annotation> ann : value) {
							AspectContext.addPoint(ann, proxyPoint);
						}
					}

					PointCut pointCut = publicMethod.getAnnotation(PointCut.class);
					if (ObjectUtil.isNotNull(pointCut)) {
						LStringAopProxyPoint point = new LStringAopProxyPoint(publicMethod, newInstance,
								pointCut.type());
						AspectContext.addStringVoidPoint(pointCut.value(), point);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(Class<T> cls) {
		if (cls == null) {
			return null;
		}
		if (cls.isAssignableFrom(this.getClass())) {
			return (T) this;
		}
		Object result = null;

		result = getObjectFromSet(cls, singleBeanSet);
		if (result != null) {
			return (T) result;
		}
		for (Map.Entry<String, BeanInitializationDefinition> entry : initializationDefinitionMap.entrySet()) {
			BeanInitializationDefinition initDefine = entry.getValue();
			if (cls.isAssignableFrom(initDefine.getSrcClass())) {
				BeanDefinition definition = getBeanByDefinition(initDefine);
				return (T) definition.getInstance();
			}
		}

		return (T) result;
	}

	/** synchronized */
	Object getObjectFromSet(Class<?> cls, Set<BeanDefinition> definitionSet) {
		for (BeanDefinition definition : definitionSet) {
			if (cls.isAssignableFrom(definition.getSourceClass())) {
				return definition.getInstance();
			}
		}
		return null;
	}

	private /** synchronized */
	void putSingleBean(BeanDefinition beanDefinition) {
		singleBeanSet.add(beanDefinition);
		initedBeanName.add(beanDefinition.getName());
	}

	@Override
	public Object getBean(String beanName) {
		for (BeanDefinition beanDefinition : singleBeanSet) {
			if (beanDefinition.getName().equals(beanName)) {
				return beanDefinition.getInstance();
			}
		}
		BeanInitializationDefinition definition = initializationDefinitionMap.get(beanName);
		if (ObjectUtil.isNotNull(definition)) {
			BeanDefinition definition1 = getBeanByDefinition(definition);
			return definition1.getInstance();
		}
		return null;
	}

	@Override
	public void register(Class<?> cls) throws Exception {

	}

	public void scan(String[] path, BeanScannerFilter filter) {
		for (String s : path) {
			try {
				// classDefinitionSet.addAll(scanClasses(s, filter));
				scanClasses(s, filter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void scanBeanInitializingDefinition() throws BeanDuplicationException {
		for (ClassDefinition classDefinition : classDefinitionSet) {
			scanOneBeanInitializingDefinition(classDefinition);
		}
	}

	private void scanOneBeanInitializingDefinition(ClassDefinition classDefinition) throws BeanDuplicationException {
		// DefNameScaned.add(classDefinition.getBeanName());
		BeanInitializationDefinition definition = new BeanInitializationDefinition(classDefinition.getBeanName(),
				classDefinition.getTarget());
		definition.setSingle(classDefinition.isSingle());
		definition.setInitOnlyOnUse(classDefinition.isInitOnlyOnUse());
		initializationDefinitionMap.put(classDefinition.getBeanName(), definition);
		getInitializingDefinitionOnMethod(classDefinition);
	}

	/**
	 * 扫描获取路径下符合的类
	 *
	 * @param path
	 * @param filter
	 * @return
	 * @throws IllegalAccessException
	 * @throws Exception
	 */
	public /* Set<ClassDefinition> */ void scanClasses(String path, BeanScannerFilter filter)
			throws IllegalAccessException, Exception {
		Assert.notBlank(path);
		// Set<ClassDefinition> classDefinitionSet = new HashSet<>();

		Set<Class<?>> classSet = new HashSet<>(ClassScaner.scanPackage(path));
		for (Class<?> aClass : classSet) {
			AopProxy aopAnnotation = AnnotationUtil.getAnnotation(aClass, AopProxy.class);
			if (aopAnnotation != null) {
				aopPointClass.add(aClass);
			}
			if (!filter.accept(aClass)) {
				/**
				 * 被过滤
				 */
				continue;
			}
			/* PostProcessor不注入容器 */
			if (addIfPostProcess(aClass)) {
				continue;
			}
			if (BeanInitRegisterExternal.class.isAssignableFrom(aClass)) {
				BeanInitRegisterExternal register = (BeanInitRegisterExternal) aClass.newInstance();
				ReflectUtil.invoke(register, "setRegister", initDefineRegister);
				initRegisterSet.add(register);
				continue;
			}
			Class<?> target = filter.getBeanType(aClass);
			/**
			 * Import的类
			 */
			// Set<ClassDefinition> importDefinitionSet = new HashSet<>();
			// 只对项目中的类扫描多注解，其他项目不扫描，只获取第一个取得的import
			Annotation[] annotations = target.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType() == Import.class) {
					Import im = (Import) annotation;
					for (Class<?> aClass1 : im.value()) {
						testImportClassDefinition(aClass1);
					}
				} else {
					Class<? extends Annotation> annotationType = annotation.annotationType();
					Import anImport = AnnotationUtil.getAnnotation(annotationType, Import.class);
					if (anImport != null) {
						Class<?>[] value = anImport.value();
						for (Class<?> aClass1 : value) {
							testImportClassDefinition(aClass1);
						}
					}
				}
			}

			// getImportClassDefinition(target, importDefinitionSet);

			boolean single = filter.single(aClass);
			// classDefinitionSet.add(new ClassDefinition(name, target));
			classDefinitionSet.add(new ClassDefinition(BeanNameUtil.getBeanName(target), target, single, false));

			// classDefinitionSet.addAll(importDefinitionSet);
		}

		// return classDefinitionSet;
	}

	/**
	 * 扫描类的时候把postProcess找出来，这些类不注入容器，并且要有无参构造方法<br/>
	 * 两种postProcess
	 *
	 * @param cls
	 * @return
	 */
	private boolean addIfPostProcess(Class<?> cls) {
		boolean result = false;
		try {
			if (PostProcessor.class.isAssignableFrom(cls)) {
				processorList.add((PostProcessor) cls.newInstance());
				result = true;
			}
			if (FieldPostProcessor.class.isAssignableFrom(cls)) {
				fieldProcessorList.add((FieldPostProcessor) cls.newInstance());
				result = true;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void testImportClassDefinition(Class<?> cls) {

		Import annotation = AnnotationUtil.getAnnotation(cls, Import.class);
		if (ObjectUtil.isNotNull(annotation)) {
			Class<?>[] value = annotation.value();
			for (Class<?> aClass : value) {
				testImportClassDefinition(aClass);
				/* PostProcessor不注入容器 */
			}
		}
		addIfPostProcess(cls);
		boolean single = true;
		Bean bean = AnnotationUtil.getAnnotation(cls, Bean.class);
		if (bean != null) {
			single = bean.single();
		}
		if (BeanInitRegisterExternal.class.isAssignableFrom(cls)) {
			BeanInitRegisterExternal register = null;
			try {
				register = (BeanInitRegisterExternal) cls.newInstance();
				ReflectUtil.invoke(register, "setRegister", initDefineRegister);

				initRegisterSet.add(register);
				return;
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		classDefinitionSet.add(new ClassDefinition(BeanNameUtil.getBeanName(cls), cls, single, false));

	}

	/**
	 * 递归寻找类上的@Import注解，同样准备注入
	 *
	 * @param cls
	 * @param classDefinitions
	 */
	public void getImportClassDefinition(Class<?> cls, Set<ClassDefinition> classDefinitions) {

		Import annotation = AnnotationUtil.getAnnotation(cls, Import.class);
		if (ObjectUtil.isNotNull(annotation)) {
			Class<?>[] value = annotation.value();
			for (Class<?> aClass : value) {
				getImportClassDefinition(aClass, classDefinitions);
				/* PostProcessor不注入容器 */
			}
		}
		addIfPostProcess(cls);
		boolean single = true;
		Bean bean = AnnotationUtil.getAnnotation(cls, Bean.class);
		if (bean != null) {
			single = bean.single();
		}
		if (BeanInitRegisterExternal.class.isAssignableFrom(cls)) {
			BeanInitRegisterExternal register = null;
			try {
				register = (BeanInitRegisterExternal) cls.newInstance();
				ReflectUtil.invoke(register, "setRegister", initDefineRegister);
				initRegisterSet.add(register);
				return;
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		classDefinitions.add(new ClassDefinition(BeanNameUtil.getBeanName(cls), cls, single, false));

	}

	private void initChildrenDepend(String name) throws IllegalAccessException {
		if (initedBeanName.contains(name)) {
			return;
		}
		if (!(name == null)) {
			BeanInitializationDefinition definition = initializationDefinitionMap.get(name);
			if (definition != null) {
				Set<String> next = definition.getDependBeans();
				for (String string : next) {
					initChildrenDepend(string);
					/* 如果为空就找到了没有依赖的bean */
				}
				BeanInitializationDefinition node = initializationDefinitionMap.get(name);
				doInjectBean(node);
			}
		}
	}

	private Set<BeanInitializationDefinition> getInitializingDefinitionOnMethod(ClassDefinition cls)
			throws BeanDuplicationException {
		Set<BeanInitializationDefinition> classDefinitionSet = new HashSet<>();
		for (Method declaredMethod : cls.getTarget().getMethods()) {
			Bean bean = AnnotationUtil.getAnnotation(declaredMethod, Bean.class);
			if (null != bean) {
				Class<?> returnType = declaredMethod.getReturnType();
				String name = returnType.getName();
				if (bean.name().length > 1) {
					name = bean.name()[0];
				}
				BeanInitializationDefinition definition = new BeanInitializationDefinition(name, cls.getTarget(),
						returnType, declaredMethod);
				definition.setSingle(bean.single());
				definition.setInitOnlyOnUse(bean.initOnlyOnUse());
				initializationDefinitionMap.put(name, definition);
				classDefinitionSet.add(definition);
			}
		}
		return classDefinitionSet;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> List<T> getBeanAssignableFromClass(Class<T> cls) {
		final List<T> list = new LinkedList();

		initializationDefinitionMap.forEach((name, initDefine) -> {
			if (cls.isAssignableFrom(initDefine.getSrcClass())) {
				Object object = getBean(cls);
				list.add((T) object);
			}
		});
		return list;
	}

	@Override
	public boolean contain(Class<?> beanClass) {
		Set<Map.Entry<String, BeanInitializationDefinition>> entries = initializationDefinitionMap.entrySet();
		for (Map.Entry<String, BeanInitializationDefinition> entry : entries) {
			if (beanClass.isAssignableFrom(entry.getValue().getSrcClass())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据扫描到的bean定义进行注入
	 */
	private void injectBean() {
		Set<Map.Entry<String, BeanInitializationDefinition>> entries = initializationDefinitionMap.entrySet();
		for (Map.Entry<String, BeanInitializationDefinition> entry : entries) {
			if (entry.getValue().isInitOnlyOnUse()) {
				continue;
			}

			Set<String> dependBeans = entry.getValue().getDependBeans();
			for (String dependBean : dependBeans) {
				try {
					initChildrenDepend(dependBean);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			if (initedBeanName.contains(entry.getKey())) {
				continue;
			}
			doInjectBean(entry.getValue());
		}
	}

	private BeanDefinition getBeanByDefinition(BeanInitializationDefinition definition) {
		BeanDefinition result = null;
		if (definition.getInitType() == InitType.NON_PARAM_METHOD) {
			String targetBeanName = definition.getTargetBeanName();
			Object bean = getBean(targetBeanName);
			Method method = definition.getInitMethod();
			Object beanInstance = ReflectUtil.invoke(bean, method);
			result = new BeanDefinition(definition.getSrcClass(), definition.getBeanName(), beanInstance);
			beanInstance = doPostProcessor(beanInstance, result);
			result.setInstance(beanInstance);
			if (definition.isSingle()) {
				putSingleBean(result);

			}
		} else {
			Object beanInstance = definition.getBeanInstance();
			prepareBean(beanInstance);
			result = new BeanDefinition(definition.getSrcClass(), definition.getBeanName(), beanInstance);

			beanInstance = doPostProcessor(beanInstance, result);
			result.setInstance(beanInstance);
			if (definition.isSingle()) {
				putSingleBean(result);
			}
		}
		return result;
	}

	private void doInjectBean(BeanInitializationDefinition definition) {
		if (initedBeanName.contains(definition.getBeanName())) {
			return;
		}
		BeanDefinition beanDefinition = null;
		if (definition.getInitType() == InitType.NON_PARAM_METHOD) {
			/**
			 * 如果这个bean是某个类的方法注入的，就先初始化 类 ，然后把所有的 方法上的bean注入
			 */

			String targetBeanName = definition.getTargetBeanName();
			BeanInitializationDefinition beanInitializationDefinition = initializationDefinitionMap.get(targetBeanName);
			doInjectBean(beanInitializationDefinition);

		} else {
			Object beanInstance = definition.getBeanInstance();

			prepareBean(beanInstance);
			beanDefinition = new BeanDefinition(definition.getSrcClass(), definition.getBeanName(), beanInstance);

			beanInstance = doPostProcessor(beanInstance, beanDefinition);
			beanDefinition.setInstance(beanInstance);

			InjectBeanOnMethod(beanInstance);
			if (definition.isSingle()) {
				putSingleBean(beanDefinition);
			}
			if (beanDefinition.getInstance() instanceof InitializingBean) {
				InitializingBean initializingBean = (InitializingBean) beanInstance;
				initializingBean.afterPropsSet();
			}
			// return beanDefinition;
		}
		// 代码进入这里意味着依赖于方法上的bean已经被注册了
		// BeanDefinition definition1 = new BeanDefinition();
		// Object bean = getBean(definition.getSrcClass());
		// definition1.setName(definition.getBeanName());
		// definition1.setSourceClass(definition.getSrcClass());
		// definition1.setInstance(bean);
		// return definition1;
	}

	/**
	 * 分别调用方法设置字段和setter方法上的依赖
	 *
	 * @param bean
	 */
	private void prepareBean(Object bean) {
		propertyField(bean);
		propertyGetter(bean);
	}

	/**
	 * 处理在字段上的注解
	 *
	 * @param bean
	 *            处理的bean
	 */
	private void propertyField(Object bean) {
		try {
			// 获取其全部的字段描述
			Field[] fields = bean.getClass().getDeclaredFields();
			for (Field field : fields) {
				InjectValueFromProps(bean, field);

				doFieldPostProcess(bean, field);

				InjectFieldValue(bean, field);
			}
		} catch (Exception e) {
			LOGGER.error("[LemonContext] fieldAnnotation error,cause:{}", e.getMessage(), e);
		}
	}

	private void InjectFieldValue(Object bean, Field field) throws IllegalAccessException {
		if (field != null && field.isAnnotationPresent(Autowired.class)) {
			field.setAccessible(true);
			Object fieldValue = field.get(bean);
			if (ObjectUtil.isNotNull(fieldValue)) {
				/**
				 * 只处理不为空的
				 */
				return;
			}
			Autowired resource = field.getAnnotation(Autowired.class);
			String name = "";
			Object value = null;
			if (StrUtil.isNotBlank(resource.name())) {
				name = resource.name();
				value = getBean(name);
			}
			// 允许访问private字段
			field.setAccessible(true);
			// 把引用对象注入属性
			if (value == null) {
				Class<?> type = field.getType();
				value = getBean(type);
			}
			Assert.notNull(value, "{}名称为 {} 的bean未找到", bean.getClass(), name);
			field.set(bean, value);
		}
	}

	/**
	 * 对字段上@Value注解的内容赋值，该值是配置文件中的值
	 *
	 * @param instance
	 */
	private void InjectValueFromProps(Object instance, Field field) throws IllegalAccessException {

		if (field.isAnnotationPresent(Value.class)) {
			Value annotation = field.getAnnotation(Value.class);
			String valueName = annotation.name();
			valueName = StrUtil.isBlank(valueName) ? field.getName() : valueName;
			Class<?> fieldType = field.getType();
			Object value = PropsUtil.getInstance().getValue(fieldType, valueName);

			if (ObjectUtil.isNull(value)) {
				value = Convert.convert(fieldType, annotation.defaultValue());
				LOGGER.warn("{} -> {} 在配置文件中未找到，将使用默认值：{}", field, valueName, value);
			}

			try {
				field.setAccessible(true);
				field.set(instance, value);
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 处理在set方法加入的注解
	 *
	 * @param bean
	 *            处理的bean
	 */
	private void propertyGetter(Object bean) {
		try {
			// 获取其属性的描述
			PropertyDescriptor[] descriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
			for (PropertyDescriptor descriptor : descriptors) {
				// 获取所有set方法
				Method setter = descriptor.getWriteMethod();
				// 判断set方法是否定义了注解
				if (setter != null && setter.isAnnotationPresent(Autowired.class)) {
					// 获取当前注解，并判断name属性是否为空
					Autowired resource = setter.getAnnotation(Autowired.class);
					String name;
					Object value = null;
					if (StrUtil.isNotBlank(resource.name())) {
						// 获取注解的name属性的内容
						name = resource.name();
						value = getBean(name);
					} else { // 如果当前注解没有指定name属性,则根据类型进行匹配
						Class<?> type = setter.getParameterTypes()[0];
						value = getBean(type);
					}
					// 允许访问private方法
					setter.setAccessible(true);
					// 把引用对象注入属性
					setter.invoke(bean, value);
				}
			}

		} catch (Exception e) {
			LOGGER.info("[LemonContext] propertyAnnotation error,cause:{}", e.getMessage(), e);
		}
	}

	/**
	 * 在方法上使用@bean的类进行注入，不能有依赖
	 *
	 * @param target
	 * @throws IllegalAccessException
	 */
	private void InjectBeanOnMethod(Object target) {
		Method[] methods = target.getClass().getMethods();
		for (Method method : methods) {
			try {
				Bean beanOnMethod = AnnotationUtil.getAnnotation(method, Bean.class);
				if (ObjectUtil.isNotNull(beanOnMethod)) {

					/* 暂时支持没有参数的 */
					Class<?> returnType = method.getReturnType();
					String beanName = "";
					if (beanOnMethod.name().length >= 1) {
						beanName = beanOnMethod.name()[0];
					} else {
						beanName = returnType.getName();
					}
					Object result = method.invoke(target);
					if (beanOnMethod.single()) {
						BeanDefinition definition = new BeanDefinition(returnType, beanName, result);
						putSingleBean(definition);
					}
				}

			} catch (Exception e) {
				LOGGER.warn("Bean On Method init failed: {}", e);
			}
		}

	}

	private Object doPostProcessor(Object bean, BeanDefinition beanDefinition) {
		Object object = bean;
		for (PostProcessor postProcessor : processorList) {
			object = postProcessor.process(object, beanDefinition);
		}
		object = aopPostProcess.process(bean, beanDefinition);
		return object;
	}

	private void doFieldPostProcess(Object bean, Field field) {
		for (FieldPostProcessor fieldPostProcessor : fieldProcessorList) {
			fieldPostProcessor.process(bean, field);
		}
	}

	private void sortPostProcessor() {
		MyComparator myComparator = new MyComparator();
		processorList.sort(myComparator);
		fieldProcessorList.sort(myComparator);
	}

	private class DefaultBeanInitDefineRegister implements BeanInitDefineRegister {

		@Override
		public void register(BeanInitializationDefinition definition) {
			initializationDefinitionMap.put(definition.getBeanName(), definition);
		}
	}

}
