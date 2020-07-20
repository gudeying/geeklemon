package cn.geeklemon.core.aop.annotation;

import cn.geeklemon.core.context.annotation.Bean;

import java.lang.annotation.*;

/**
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Bean
public @interface AopProxy {
}
