package cn.geeklemon.core.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动注入配置文件中的值
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
    /**
     * 值名称
     *
     * @return name
     */
    String name() default "";

    /**
     * 配置文件中没有name时的缺省值，如果该缺省值不为空字符串，容器不会报错并且在缺失时使用
     *
     * @return
     */
    String defaultValue() default "";
}
