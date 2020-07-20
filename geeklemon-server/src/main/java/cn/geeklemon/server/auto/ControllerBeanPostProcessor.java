package cn.geeklemon.server.auto;

import cn.geeklemon.core.bean.factory.BeanDefinition;
import cn.geeklemon.core.bean.factory.PostProcessor;
import cn.geeklemon.server.controller.ControllerDefine;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.hutool.core.util.ObjectUtil;

import java.lang.reflect.Method;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/20 17:31 Modified by : kavingu
 */
public class ControllerBeanPostProcessor implements PostProcessor {
	@Override
	public Object process(Object bean, BeanDefinition beanDefinition) {
		Controller controller = beanDefinition.getSourceClass().getAnnotation(Controller.class);
		if (ObjectUtil.isNotNull(controller)) {
			// System.out.println("扫描到controller：" + controller.value());
			Method[] methods = beanDefinition.getSourceClass().getMethods();
			for (Method method : methods) {
				Mapping mapping = method.getAnnotation(Mapping.class);
				if (mapping != null) {
					// String path = mapping.path();
					// RenderType renderType = mapping.renderType();
					// RequestMethod requestMethod = mapping.requestMethod();
					// LemonServerWebContext.addController(new
					// ControllerDefine(path, method, requestMethod,
					// renderType));
					AutoWebContext.addController(new ControllerDefine(mapping, method));
				}
			}
		}
		return bean;
	}
}
