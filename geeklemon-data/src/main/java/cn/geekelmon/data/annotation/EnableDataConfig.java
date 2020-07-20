package cn.geekelmon.data.annotation;

import cn.geekelmon.data.context.DataContext;
import cn.geeklemon.core.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动扫描Mapper配置
 * @author : Kavin Gu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(DataContext.class)
public @interface EnableDataConfig {
}
