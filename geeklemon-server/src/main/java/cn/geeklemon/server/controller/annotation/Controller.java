package cn.geeklemon.server.controller.annotation;

import cn.geeklemon.core.context.annotation.Bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
/**
 * 单例的bean可以大幅减少请求时间 因此在Controller中的自定义变量要注意线程安全问题，或者使用ThreadLocal
 * 
 * 但是！但是！但是！ 一定要注意，如果使用了AOP，例如缓存、异常同一处理或者声明式的aop，设置非单例bean时请关注栈大小，否则高并发时会造成 栈溢出。
 */
@Bean(single = true)
public @interface Controller {
	String[] value() default {};

	String[] method() default {};
}
