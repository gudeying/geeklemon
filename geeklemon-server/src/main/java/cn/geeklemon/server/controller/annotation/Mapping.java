package cn.geeklemon.server.controller.annotation;

import cn.geeklemon.server.common.RenderType;
import cn.geeklemon.server.common.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注http请求由哪个方法执行<br/>
 * 需要注意方法中的参数如果是基础数据类型，必须使用@Param来标识{@link Param}
 * 如果前台数据需要封装为bean，直接在方法参数中添加，但是不会参与请求匹配，该bean不会为null，但是内容可能为空
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {

    /**
     * 请求方法类型
     *
     * @return 方法类型
     * @see RequestMethod
     */
    RequestMethod requestMethod() default RequestMethod.ANY;

    /**
     * 请求的uri
     *
     * @return url
     */
    String path() default "";

    /**
     * 返回类型
     *
     * @return 返回类型
     * @see RenderType
     */
    RenderType renderType() default RenderType.JSON;

    /**
     * @return 是否允许跨域访问
     */
    boolean CrossOrigin() default false;

    /**
     * 必须带有的请求头
     *
     * @return 请求头数组
     */
    String[] header() default {};


}