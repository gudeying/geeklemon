package cn.geeklemon.server.auto;

import cn.geeklemon.core.context.annotation.Import;
import cn.geeklemon.server.LemonServer;
import cn.geeklemon.server.viewrender.TemplateViewEngine;
import cn.geeklemon.server.viewrender.ViewEngineConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动http服务器以及controller自动扫描
 * 
 * @author : Kavin Gu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ ServerAutoConfig.class, ControllerBeanPostProcessor.class, ViewEngineConfig.class })
public @interface WebApplication {
}
