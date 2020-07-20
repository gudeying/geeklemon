package cn.geeklemon.core.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动注入依赖
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    /**
     * bean名称，如果配置了此项，将根据名称获取bean，这可能产生异常
     *
     * @return
     */
    String name() default "";

}
