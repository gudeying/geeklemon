package cn.geeklemon.core.aop.support;

import cn.geeklemon.core.aop.extra.ExceptionAvoid;
import cn.geeklemon.core.aop.extra.ExceptionHandler;
import cn.geeklemon.core.aop.extra.ExceptionPoint;
import cn.geeklemon.core.aop.extra.NullDefaultExceptionHandler;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.util.PrimitiveTypeUtil;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

public class LProxyFactory {
	private static LProxyFactory instance = new LProxyFactory();
	static FIFOCache<Object, Object> exceptionHandlerCache = CacheUtil.newFIFOCache(100);

	static {
		exceptionHandlerCache.put(NullDefaultExceptionHandler.class, new NullDefaultExceptionHandler());
	}

	private LProxyFactory() {
	}

	public static LProxyFactory create() {
		return instance;
	}

	public Object getProxy(Object origin, List<VoidPoint> voidPointList, ApplicationContext context) {
		final Enhancer en = new Enhancer();
		en.setSuperclass(origin.getClass());

		ProxyChain proxyChain = new ProxyChain();

		if (ObjectUtil.isNotNull(voidPointList)) {
			voidPointList.sort(voidPointComparator);
		}

		proxyChain.setVoidPointList(voidPointList);
		en.setCallback(new Interceptor(proxyChain, origin, context));

		return en.create();
	}

	private class Interceptor implements MethodInterceptor {
		ApplicationContext context;
		ProxyChain chain;
		private Object origin;

		Interceptor(ProxyChain chain, Object origin, ApplicationContext context) {
			this.chain = chain;
			this.origin = origin;
			this.context = context;
		}

		@Override
		public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
			try {
				ProxyResult proxyResult = chain.proceed(origin, method, params, methodProxy, null);
				if (proxyResult != null) {
					Object result = proxyResult.getResult();
					if (proxyResult.exceptionCase()) {
						ExceptionAvoid exceptionAvoid = AnnotationUtil.getAnnotation(method, ExceptionAvoid.class);
						if (exceptionAvoid != null) {
							Class<? extends ExceptionHandler> handlerClass = exceptionAvoid.handler();
							ExceptionHandler exceptionHandler = getExceptionHandler(handlerClass);
							if (exceptionHandler != null) {
								ExceptionPoint point = new ExceptionPoint(o, method, params,
										getTargetException(proxyResult.getException()));
								return exceptionHandler.handle(point);
							}
						} else {
							throw proxyResult.getException();
						}
					}
					return result;
				}
				Class<?> returnType = method.getReturnType();
				return getDefaultValue(returnType);
			} finally {
				chain.clear();
			}
		}

		private Throwable getTargetException(Throwable throwable) {
			if (throwable != null) {
				if (throwable instanceof InvocationTargetException) {
					InvocationTargetException exception = (InvocationTargetException) throwable;
					return exception.getTargetException();
				}
				return throwable;
			}
			return null;
		}

		private ExceptionHandler getExceptionHandler(Class<? extends ExceptionHandler> hClass) {
			try {
				ExceptionHandler object = (ExceptionHandler) exceptionHandlerCache.get(hClass);
				if (object != null) {
					return object;
				}
				object = context.getBean(hClass);
				if (object != null) {
					exceptionHandlerCache.put(hClass, object);
					return object;
				}

				object = hClass.newInstance();
				exceptionHandlerCache.put(hClass, object);
				return object;
			} catch (Exception e) {
				return null;
			}
		}
	}

	private Object getDefaultValue(Class<?> returnType) {
		if (PrimitiveTypeUtil.isPriType(returnType)) {
			if (ClassUtil.isPrimitiveWrapper(returnType)) {
				return null;
			} else {
				return PrimitiveTypeUtil.getPriDefaultValue(returnType);
			}
		}
		return null;
	}

	private static Comparator<VoidPoint> voidPointComparator = new Comparator<VoidPoint>() {
		@Override
		public int compare(VoidPoint o1, VoidPoint o2) {
			return o1.getSortCode() - o2.getSortCode();
		}
	};
}