package cn.geekelmon.cache.context;

import cn.geekelmon.cache.annotation.LCache;
import cn.geeklemon.core.aop.AspectContext;
import cn.geeklemon.core.bean.factory.BeanDefinition;
import cn.geeklemon.core.bean.factory.PostProcessor;
import cn.geeklemon.core.util.PropsUtil;
import cn.hutool.core.date.DateUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class LCacheAutoConfig implements PostProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(LCacheAutoConfig.class);
	private long timeout = DateUnit.SECOND.getMillis() * 60;// 一分钟
	private long pruneDelay = DateUnit.MINUTE.getMillis() * 30;// 30分钟一次，目前数据较少
	private String cacheProviderClass;

	public LCacheAutoConfig() {
		cacheProviderClass = PropsUtil.getInstance().getValue(String.class, "lemon.cache.provider");
		Long timeout = PropsUtil.getInstance().getValue(Long.class, "lemon.cache.default.timeout");
		if (timeout != null) {
			this.timeout = timeout;
		}
		Long prune = PropsUtil.getInstance().getValue(Long.class, "lemon.cache.default.pruneDelay");
		if (prune != null) {
			pruneDelay = prune;
		}
		config();
	}

	private void config() {
		LOGGER.info("[LCache] config ....");
		try {
			Class<?> aClass = Class.forName(cacheProviderClass);
			CacheProvider instance = (CacheProvider) aClass.newInstance();
			CacheContext.setProvider(instance);
		} catch (Exception e) {
			CacheContext.setProvider(new DefaultCacheProvider(timeout, pruneDelay));
			LOGGER.info("[LCache] use default cacheProvider");
		}
	}

	@Override
	public Object process(Object bean, BeanDefinition definition) {
		Class<?> sourceClass = definition.getSourceClass();
		Method[] methods = sourceClass.getMethods();
		for (Method method : methods) {
			LCache cache = method.getAnnotation(LCache.class);
			if (cache != null) {
				AspectContext.addVoidPoint(sourceClass, new CachePoint(CacheContext.getProvider()));
				LOGGER.info("LCache config : {}", sourceClass.getSimpleName());
				break;
			}
		}
		return bean;
	}
}
