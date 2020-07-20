package cn.geeklemon.core.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用方法无参方法注入需要的bean，方法必须是public
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
	String[] name() default {};

	int sortCode() default 0;

	/**
	 * 谨慎使用非单例bean。 请注意aop链式调用造成的栈溢出。
	 */
	boolean single() default true;

	boolean initOnlyOnUse() default false;
}
