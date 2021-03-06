package cn.geeklemon.server.annotaion;


import cn.geeklemon.core.context.annotation.Bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Bean
public @interface Filter {
    String[] value() default {};
}
