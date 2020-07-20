package cn.geekelmon.data.context;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geekelmon.data.annotation.LMapper;
import cn.geekelmon.data.mapper.MapperScaner;
import cn.geekelmon.data.session.JdbcExecutor;
import cn.geekelmon.data.support.DefaultJdbcExcutorFactory;
import cn.geekelmon.data.support.JdbcExecutorFactory;
import cn.geekelmon.data.support.TransactionPostProcessor;
import cn.geeklemon.core.bean.factory.BeanInitializationDefinition;
import cn.geeklemon.core.bean.factory.InitializingBean;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Value;
import cn.geeklemon.core.context.support.chain.FactoryBean;
import cn.geeklemon.core.context.support.external.BeanInitDefineRegister;
import cn.geeklemon.core.context.support.external.BeanInitRegisterExternal;
import cn.geeklemon.core.util.BeanNameUtil;
import cn.geeklemon.core.util.PropsUtil;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 */
public class DataContext implements BeanInitRegisterExternal, InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataContext.class);

	@Value(name = "lemon.data.framework.mapper.package")
	private String mapperPackages;
	@Autowired
	private DataSource dataSource;
	private BeanInitDefineRegister register;
	JdbcExecutorFactory factory;

	private List<MapperDefine> mapperDefineList = new LinkedList<>();

	@Override
	public void afterPropsSet() {
		InitContext();
		// registerBean();
	}

	@Override
	public void setRegister(BeanInitDefineRegister register) {
		this.register = register;
	}

	@Override
	public void register() {
		BeanInitializationDefinition jdbcExecutorFactory = new BeanInitializationDefinition(
				factory.getClass().getName(), new MapperFactoryBean(JdbcExecutorFactory.class, factory));
		register.register(jdbcExecutorFactory);

		for (MapperDefine mapperDefine : mapperDefineList) {
			FactoryBean factoryBean = new MapperFactoryBean(mapperDefine.getSrcClass(), mapperDefine.getResult());
			register.register(new BeanInitializationDefinition(mapperDefine.getBeanName(), factoryBean));
		}
	}

	private void InitContext() {

		factory = new DefaultJdbcExcutorFactory(dataSource);

		TransactionPostProcessor.setJdbcExecutorFactory(factory);
		if (StrUtil.isBlank(mapperPackages)) {
			mapperPackages = PropsUtil.getInstance().getValue(String.class, "mainPackage");
			LOGGER.info("mapper扫描包为空,将从main函数所在包扫描");
		}
		LOGGER.info("开始扫描Mapper");
		ClassScaner classScaner = new ClassScaner(mapperPackages, new Filter<Class<?>>() {
			@Override
			public boolean accept(Class<?> aClass) {
				LMapper lMapper = AnnotationUtil.getAnnotation(aClass, LMapper.class);
				return ObjectUtil.isNotNull(lMapper);
			}
		});
		Set<Class<?>> classSet = classScaner.scan();
		if (CollectionUtil.isEmpty(classSet)) {
			LOGGER.info("no mapper find at {}", mapperPackages);
			return;
		}
		LOGGER.info("mapper number : {}", classSet.size());

		MapperScaner.getInstance().MapperScaner(classSet);

		JdbcExecutor executor = new JdbcExecutor(dataSource);

		for (Class<?> aClass : classSet) {
			// Object proxy = ProxyFactory.getProxy(aClass, executor);
			// Object proxy = ProxyFactory.getJdkProxy(aClass, executor);
			Object proxy = ProxyFactory.managedProxy(aClass, factory);

			String beanName = BeanNameUtil.getBeanName(aClass);
			MapperDefine mapperDefine = new MapperDefine(beanName, aClass, proxy);
			mapperDefineList.add(mapperDefine);
		}
	}

	private void registerBean() {
		BeanInitializationDefinition factoryBean = new BeanInitializationDefinition(factory.getClass().getName(),
				new MapperFactoryBean(JdbcExecutorFactory.class, factory));
		register.register(factoryBean);
		for (MapperDefine define : mapperDefineList) {
			BeanInitializationDefinition bean = new BeanInitializationDefinition(define.getBeanName(),
					new MapperFactoryBean(define.getSrcClass(), define.getResult()));
			register.register(bean);
		}
	}
}
