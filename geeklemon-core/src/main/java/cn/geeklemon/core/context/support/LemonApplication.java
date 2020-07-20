package cn.geeklemon.core.context.support;

import cn.geeklemon.core.context.annotation.GeekLemonApplication;
import cn.geeklemon.core.util.PropsUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/9 16:50 Modified by : kavingu
 */
public class LemonApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(LemonApplication.class);

	private static String[] scanPackage;

	public static ApplicationContext run(Class cls) {
		ApplicationContext context = null;
		String mainPackage = cls.getPackage().getName();
		PropsUtil.getInstance().addValue("mainPackage", mainPackage);
		GeekLemonApplication lemonApp = (GeekLemonApplication) cls.getAnnotation(GeekLemonApplication.class);
		if (ObjectUtil.isNull(lemonApp) || ArrayUtil.isEmpty(lemonApp.scanPackage())) {
			scanPackage = new String[] { mainPackage };
		} else {
			scanPackage = lemonApp.scanPackage();
		}
		try {
			context = startContext();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context;
	}

	private static ApplicationContext startContext() throws Exception {
		return LemonContext.getInstance().init(scanPackage);
	}
}
