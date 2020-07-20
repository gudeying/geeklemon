package cn.geeklemon.core.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.geeklemon.core.aop.extra.ExceptionPostProssor;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Bean
@Import(ExceptionPostProssor.class)
public @interface GeekLemonApplication {
	String[] scanPackage() default {};
}
