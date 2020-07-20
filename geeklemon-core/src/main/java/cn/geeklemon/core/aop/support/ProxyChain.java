package cn.geeklemon.core.aop.support;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import io.netty.util.concurrent.FastThreadLocal;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

public class ProxyChain {
	private List<VoidPoint> list;
	private FastThreadLocal<ProxyResult> resultLocal = new FastThreadLocal<>();
	private FastThreadLocal<Integer> indexLocal = new FastThreadLocal<>();

	public ProxyResult proceed(Object target, Method method, Object[] params, MethodProxy methodProxy,
			PointResult pointResult) throws Throwable {
		ProxyResult result;
		if (ObjectUtil.isNotNull(pointResult) && pointResult.forceReturn()) {
			indexLocal.set(-1);
			result = new DefaultProxyResult(pointResult.getResult(), null);
			resultLocal.set(result);
			return result;
		}
		if (ObjectUtil.isNull(list)) {
			try {
				Object invoke = ReflectUtil.invoke(target, method, params);
				result = new DefaultProxyResult(invoke);
			} catch (UtilException e) {
				result = new DefaultProxyResult(e.getCause());
			}
			resultLocal.set(result);
			return result;
		}
		Integer integer = indexLocal.get();
		if (integer == null) {
			integer = -1;
		}
		indexLocal.set(++integer);
		Integer increase = indexLocal.get();
		if (increase >= list.size()) {
			indexLocal.set(-1);
			try {
				Object invoke = ReflectUtil.invoke(target, method, params);
				result = new DefaultProxyResult(invoke);
			} catch (UtilException e) {
				result = new DefaultProxyResult(e.getCause());
			}
			resultLocal.set(result);
			return result;
		} else {
			VoidPoint point = list.get(increase);
			point.proceed(target, method, params, this, methodProxy, pointResult);
		}
		/**
		 * 下面才是最后一次返回
		 */
		ProxyResult finalResult = resultLocal.get();
		// resultLocal.remove();
		// indexLocal.remove();
		return finalResult;
	}

	public ProxyChain setVoidPointList(List<VoidPoint> voidPointList) {
		this.list = voidPointList;
		return this;
	}

	private class DefaultProxyResult implements ProxyResult {
		private Object result;
		private Throwable exception;

		public DefaultProxyResult() {
		}

		DefaultProxyResult(Object result) {
			this.result = result;
		}

		public DefaultProxyResult(Throwable exception) {
			this.exception = exception;
		}

		DefaultProxyResult(Object result, Exception exception) {
			this.result = result;
			this.exception = exception;
		}

		@Override
		public Object getResult() {
			return result;
		}

		@Override
		public Throwable getException() {
			return exception;
		}
	}

	public void clear() {
		indexLocal.remove();
		resultLocal.remove();
	}
}
