package cn.geeklemon.core.aop.extra;

import java.lang.reflect.Method;

import cn.geeklemon.core.aop.support.PointResult;
import cn.geeklemon.core.aop.support.ProxyChain;
import cn.geeklemon.core.aop.support.VoidPoint;
import net.sf.cglib.proxy.MethodProxy;

public class ExceptionHandlerPoint implements VoidPoint {

	@Override
	public void proceed(Object target, Method method, Object[] args, ProxyChain chain, MethodProxy methodProxy,
			PointResult pointResult) throws Throwable {
		chain.proceed(target, method, args, methodProxy, pointResult);
	}

}
