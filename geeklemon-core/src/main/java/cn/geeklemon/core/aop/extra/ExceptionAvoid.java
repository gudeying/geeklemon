package cn.geeklemon.core.aop.extra;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解用于容器管理的方法上，当异常发生时调用handler处理然后返回handler处理的结果<br/>
 * 不会抛出异常<br/>
 * 该拦截不影响其他自定义的拦截器对异常的处理。比如data项目的事务回滚。因为它在最后一层<br/>
 * 注意会对handler进行缓存，所以如如果有共享变量记得处理线程安全
 * 
 * @author Goldin
 *
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionAvoid {
	Class<? extends ExceptionHandler> handler() default NullDefaultExceptionHandler.class;
}
