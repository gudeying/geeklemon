package cn.geekelmon.cache.annotation;

import cn.geekelmon.cache.key.CacheKeyGenerator;
import cn.geekelmon.cache.key.DefaultCacheKeyGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标志对该方法使用缓存
 * 使用默认的缓存key请注意目标类和参数的equals和hashCode
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LCache {
    /**
     * 缓存过期时间
     * 默认10分钟
     * 默认的缓存是无限存储的，过期时间最好根据实际设置
     *
     * @return 过期时间
     */
    long timeOut() default 1000 * 60 * 10;

    /**
     * 缓存命中是否打印log日志
     */
    boolean log() default false;

    /**
     * cacheKey的生成方式
     *
     * @return key生成方式
     */
    Class<? extends CacheKeyGenerator> keyGen() default DefaultCacheKeyGenerator.class;
}
