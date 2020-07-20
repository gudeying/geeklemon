package cn.geeklemon.server;

import cn.geeklemon.server.request.LemonHttpRequest;
import cn.geeklemon.server.response.LemonHttpResponse;
import cn.geeklemon.server.viewrender.ModelAndView;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.concurrent.FastThreadLocal;

import java.util.*;

/**
 * TemporaryDataHolder 将请求消息对象存放在静态ThreadLocal的实例变量中
 *
 * @author houyi.wh
 * @date 2017-10-20
 */
public class TemporaryDataHolder {

	/**
	 * 使用FastThreadLocal替代JDK自带的ThreadLocal以提升并发性能
	 */
	private static final FastThreadLocal<Map<String, Object>> FAST_THREAD_LOCAL = new FastThreadLocal<>();

	public enum HolderType {
		/**
		 * request
		 */
		REQUEST("request"),
		/**
		 * response
		 */
		RESPONSE("response"),
		/**
		 * context
		 */
		CONTEXT("context"),
		/**
		 * cookie
		 */
		COOKIE("cookie"),

		FULL_REQUEST("full_request"),

		LEMON_RESPONSE("lemonResponse"), LEMON_REQUEST("lemonRequest"), MODEL_MAP("model_map"), MODEL_AND_VIEW(
				"model_and_view");
		private String type;

		HolderType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}

	// ===================================

	private static Map<String, Object> getLocalMap() {
		Map<String, Object> localMap = FAST_THREAD_LOCAL.get();
		if (localMap == null) {
			/**
			 * hashMap对相同的key最后的值会覆盖之前的
			 */
			localMap = new HashMap<>();
		}
		return localMap;
	}

	public static void store(HolderType holderType, Object value) {
		Map<String, Object> localMap = getLocalMap();
		localMap.put(holderType.getType(), value);
		FAST_THREAD_LOCAL.set(localMap);
	}

	private static Object get(HolderType holderType) {
		Map<String, Object> localMap = getLocalMap();
		return localMap.get(holderType.getType());
	}

	private static void remove(HolderType holderType) {
		Map<String, Object> localMap = getLocalMap();
		localMap.remove(holderType.getType());
		FAST_THREAD_LOCAL.set(localMap);
	}

	public static void removeAll() {
		FAST_THREAD_LOCAL.remove();
	}

	public static void storeHttpRequest(HttpRequest httpRequest) {
		if (httpRequest != null) {
			TemporaryDataHolder.store(HolderType.REQUEST, httpRequest);
		}
	}

	public static void storeHttpResponse(HttpResponse httpResponse) {
		if (httpResponse != null) {
			TemporaryDataHolder.store(HolderType.RESPONSE, httpResponse);
		}
	}

	public static void storeFullHttpRequest(FullHttpRequest request) {
		if (request != null) {
			TemporaryDataHolder.store(HolderType.FULL_REQUEST, request);
		}
	}

	public static void storeContext(ChannelHandlerContext context) {
		if (context != null) {
			TemporaryDataHolder.store(HolderType.CONTEXT, context);
		}
	}

	/**
	 * 添加一个cookie,一般是响应的地方添加
	 *
	 * @param cookie
	 */
	public static void storeCookie(Cookie cookie) {
		if (cookie != null) {
			Set<Cookie> cookies = TemporaryDataHolder.loadCookies();
			if (CollectionUtil.isEmpty(cookies)) {
				cookies = new HashSet<>();
			}
			cookies.add(cookie);
			TemporaryDataHolder.store(HolderType.COOKIE, cookies);
		}
	}

	public static HttpRequest loadHttpRequest() {
		Object object = TemporaryDataHolder.get(HolderType.REQUEST);
		return object == null ? null : (HttpRequest) object;
	}

	public static FullHttpResponse loadHttpResponse() {
		Object object = TemporaryDataHolder.get(HolderType.RESPONSE);
		return object == null ? null : (FullHttpResponse) object;
	}

	public static FullHttpRequest loadFullHttpRequest() {
		Object object = TemporaryDataHolder.get(HolderType.FULL_REQUEST);
		return object == null ? null : (FullHttpRequest) object;
	}

	public static ChannelHandlerContext loadContext() {
		Object object = TemporaryDataHolder.get(HolderType.CONTEXT);
		return object == null ? null : (ChannelHandlerContext) object;
	}

	@SuppressWarnings("unchecked")
	public static Set<Cookie> loadCookies() {
		Object object = TemporaryDataHolder.get(HolderType.COOKIE);
		return object == null ? null : (Set<Cookie>) object;
	}

	/**
	 * 实际上不需要额外再保存cookie
	 * 
	 * @param request
	 */
	@Deprecated
	public static void storeCookies(HttpRequest request) {
		Set<Cookie> cookies = new HashSet<>();
		if (request != null) {
			String value = request.headers().get(HttpHeaderNames.COOKIE);
			if (value != null) {
				cookies = ServerCookieDecoder.STRICT.decode(value);
			}
		}
		store(HolderType.COOKIE, cookies);
	}

	/**
	 * 返回或者创建并存储返回
	 *
	 * @return request，不会为空
	 */
	public static cn.geeklemon.server.request.HttpRequest loadLemonRequest() {
		Object object = get(HolderType.LEMON_REQUEST);
		if (ObjectUtil.isNotNull(object)) {
			return (cn.geeklemon.server.request.HttpRequest) object;
		}
		LemonHttpRequest lemonHttpRequest = new LemonHttpRequest();
		store(HolderType.LEMON_REQUEST, lemonHttpRequest);
		return lemonHttpRequest;
	}

	/**
	 * 返回或者创建并存储返回
	 *
	 * @return 不会为空
	 */
	public static cn.geeklemon.server.response.HttpResponse loadLemonResponse() {
		Object object = get(HolderType.LEMON_RESPONSE);
		if (ObjectUtil.isNotNull(object)) {
			return (cn.geeklemon.server.response.HttpResponse) object;
		}
		cn.geeklemon.server.response.HttpResponse response = new LemonHttpResponse();
		store(HolderType.LEMON_RESPONSE, response);
		return response;
	}

	/**
	 * 保存用户modelAndView的map，如果已有，则返回，如果没有，则创建并保存，然后返回该Map
	 *
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> loadModelMap() {
		Object object = get(HolderType.MODEL_MAP);
		if (ObjectUtil.isNotNull(object)) {
			return (Map<String, Object>) object;
		}
		HashMap<String, Object> hashMap = new HashMap<>();
		store(HolderType.MODEL_MAP, hashMap);
		return hashMap;
	}

	public static ModelAndView loadModelAndView() {
		Object object = get(HolderType.MODEL_AND_VIEW);
		if (ObjectUtil.isNotNull(object)) {
			return (ModelAndView) object;
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setModel(loadModelMap());
		store(HolderType.MODEL_AND_VIEW, modelAndView);
		return modelAndView;
	}

	public static void storeLemonRequest(cn.geeklemon.server.request.HttpRequest request) {
		if (request != null) {
			TemporaryDataHolder.store(HolderType.LEMON_REQUEST, request);
		}
	}
}
