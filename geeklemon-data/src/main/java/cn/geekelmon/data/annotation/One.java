package cn.geekelmon.data.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : Kavin Gu
 * Project Name : redant
 * Description :
 * @version : ${VERSION} 2019/3/12 10:10
 * Modified by : kavingu
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface One {
    String column();
    String row();
}
