package cn.geekelmon.cache.context;

import cn.geekelmon.cache.key.CacheKeyGenerator;
import cn.geekelmon.cache.key.DefaultCacheKeyGenerator;
import cn.hutool.core.date.DateUnit;

public class CacheContext {
    private static CacheProvider provider;
    private static CacheKeyGenerator cacheKeyGenerator;

    static {
        cacheKeyGenerator = new DefaultCacheKeyGenerator();
    }

    public static CacheProvider getProvider() {
        if (provider == null) {
            return provider = new DefaultCacheProvider(DateUnit.MINUTE.getMillis() * 30, DateUnit.MINUTE.getMillis() * 30);
        }
        return provider;
    }

    public static void setProvider(CacheProvider provider) {
        CacheContext.provider = provider;
    }

    public static CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    public static void setCacheKeyGenerator(CacheKeyGenerator cacheKeyGenerator) {
        CacheContext.cacheKeyGenerator = cacheKeyGenerator;
    }
}
