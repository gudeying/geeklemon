package cn.geeklemon.core.aop.annotation;

import cn.geeklemon.core.aop.AopType;

import java.lang.annotation.*;

/**
 * 注解的方法必须是public
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AopPoint {
    AopType type() default AopType.AROUND;

    Class<? extends Annotation>[] value() default {};
}
