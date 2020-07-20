package cn.geeklemon.server.intercepter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WebAppInterceptor {
	/**
	 * path匹配 支持*通配符，**用在结尾通配
	 * 
	 * @return
	 */
	String[] value() default {};
}
