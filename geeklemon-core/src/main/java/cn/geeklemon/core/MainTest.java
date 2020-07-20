package cn.geeklemon.core;

import cn.geeklemon.core.context.annotation.GeekLemonApplication;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.Method;
import io.netty.util.internal.ReflectionUtil;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/10 9:27 Modified by : kavingu
 */
@GeekLemonApplication
public class MainTest {
	public static void testRegx() {
		// String inputRegex = "com.project.dao.impl.*.*()";
		String inputRegex = "com.project.dao.impl.*.save*()";
		String regex2 = inputRegex.trim().replace(" ", "\\s+").replace(".", "\\.").replace("*", "\\w+")
				.replace("(", "\\(").replace(")", "\\)");
		String input2 = "com.project.dao.impl.TestCla.saveA()";
		System.out.println(regex2);
		System.out.println(ReUtil.isMatch(regex2, input2));
	}

	public static void main(String[] args) {
		java.lang.reflect.Method voidM = ClassUtil.getPublicMethod(MainTest.class, "test",
				new Class[] { String.class });
		System.out.println(voidM);
		Class<?> returnType = voidM.getReturnType();
		System.out.println(void.class.isAssignableFrom(returnType));
		System.out.println(voidM.getReturnType());
	}

	public void test(String anem) {

	}
}
