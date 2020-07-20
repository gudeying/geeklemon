package cn.geekelmon.cache.context;

import cn.geekelmon.cache.annotation.LCache;
import cn.geekelmon.cache.key.CacheKeyGenerator;
import cn.geekelmon.cache.key.DefaultCacheKeyGenerator;
import cn.geeklemon.core.aop.support.PointResult;
import cn.geeklemon.core.aop.support.ProxyChain;
import cn.geeklemon.core.aop.support.ProxyResult;
import cn.geeklemon.core.aop.support.VoidPoint;
import cn.hutool.core.util.ObjectUtil;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class CachePoint implements VoidPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachePoint.class);
    private CacheProvider CacheHolder;

    public CachePoint(CacheProvider cacheHolder) {
        CacheHolder = cacheHolder;
    }

    @Override
    public void proceed(Object o, Method method, Object[] args, ProxyChain chain, MethodProxy methodProxy, PointResult pointResult) throws Throwable {
        LCache lCache = method.getAnnotation(LCache.class);
        if (ObjectUtil.isNotNull(lCache)) {

            Class<? extends CacheKeyGenerator> aClass = lCache.keyGen();
            boolean log = lCache.log();
            long timeOut = lCache.timeOut();
            Object key = genKey(aClass, o, method, args);
            Object cacheValue = CacheHolder.getCacheValue(key);
            Class<?> returnType = method.getReturnType();
            if (ObjectUtil.isNotNull(cacheValue) && returnType.isAssignableFrom(cacheValue.getClass())) {
                if (log) {
                    LOGGER.info("{} cache attached ", method);
                }
                chain.proceed(o, method, args, methodProxy, getPointResult(cacheValue));
            } else {
                ProxyResult o1 = chain.proceed(o, method, args, methodProxy, pointResult);
                if (ObjectUtil.isNotNull(o1)) {
                    CacheHolder.putCache(key, o1.getResult(), timeOut);
                }
            }

        } else {
            chain.proceed(o, method, args, methodProxy, pointResult);
        }
    }

    private Object genKey(Class<? extends CacheKeyGenerator> generator, Object object, Method method, Object[] args) {
        if (DefaultCacheKeyGenerator.class.isAssignableFrom(generator)) {
            return CacheContext.getCacheKeyGenerator().getCacheKey(object, method, args);
        }
        try {
            CacheKeyGenerator keyGenerator = generator.newInstance();
            return keyGenerator.getCacheKey(object, method, args);
        } catch (Exception e) {
            return null;
        }
    }

    private PointResult getPointResult(Object value) {
        return new PointResult() {
            @Override
            public Object getResult() {
                return value;
            }

            @Override
            public boolean forceReturn() {
                return true;
            }
        };
    }
}
