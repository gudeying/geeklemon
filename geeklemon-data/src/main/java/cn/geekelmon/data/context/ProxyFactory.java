package cn.geekelmon.data.context;

import java.lang.reflect.Proxy;

import cn.geekelmon.data.session.JdbcExecutor;
import cn.geekelmon.data.sql.JdkSqlProxy;
import cn.geekelmon.data.sql.MapperInterceptor;
import cn.geekelmon.data.sql.SQLProxy;
import cn.geekelmon.data.support.JdbcExecutorFactory;
import net.sf.cglib.proxy.Enhancer;

/**
 */
public class ProxyFactory {
	public static Object getProxy(Class<?> mapper, JdbcExecutor executor) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(mapper);
		enhancer.setCallback(new SQLProxy(executor));
		Object o = enhancer.create();
		return o;
	}

	public static Object getJdkProxy(Class<?> mapper, JdbcExecutor executor) {
		Object object = Proxy.newProxyInstance(ProxyFactory.class.getClassLoader(), new Class<?>[] { mapper },
				new JdkSqlProxy(executor));
		return object;
	}

	public static Object managedProxy(Class<?> mapper, JdbcExecutorFactory factory) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(mapper);
		enhancer.setCallback(new MapperInterceptor(factory));
		Object o = enhancer.create();
		return o;
	}
}
