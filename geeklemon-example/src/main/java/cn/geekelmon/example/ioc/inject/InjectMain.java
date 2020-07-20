package cn.geekelmon.example.ioc.inject;

import cn.geeklemon.core.context.annotation.GeekLemonApplication;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.context.support.LemonApplication;

/**
 * 测试同类型bean根据名称进行获取
 * 
 * @author Goldin
 *
 */
@GeekLemonApplication
public class InjectMain {
	public static void main(String[] args) {
		ApplicationContext context = LemonApplication.run(InjectMain.class);
	}
}
