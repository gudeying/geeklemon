package cn.geeklemon.core.aop.annotation;

import cn.geeklemon.core.aop.AopType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解的方法必须时public
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PointCut {
    String value();
    AopType type() default AopType.AROUND;
}
