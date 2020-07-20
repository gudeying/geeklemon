package cn.geekelmon.cache.annotation;

import cn.geekelmon.cache.context.LCacheAutoConfig;
import cn.geeklemon.core.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动扫描容器中的Lcache拦截
 * 
 * @author Goldin
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LCacheAutoConfig.class)
public @interface EnableCache {
}
