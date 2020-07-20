package cn.geeklemon.server.websocket.context;

import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.util.PrimitiveTypeUtil;
import cn.geeklemon.server.controller.annotation.Param;
import cn.geeklemon.server.websocket.annotation.ServerEndPoint;
import cn.geeklemon.server.websocket.annotation.WebSocketEventHandler;
import cn.geeklemon.server.websocket.param.UriParameterUtil;
import cn.geeklemon.server.websocket.param.WebSocketParamHolder;
import cn.geeklemon.server.websocket.param.WebSocketUrlParam;
import cn.geeklemon.server.websocket.support.WebSocketEndPointDefine;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.ClassScaner;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/10/22 10:32 Modified by : kavingu
 */
public class EndPointHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(EndPointHelper.class);

	private static final Set<WebSocketEndPointDefine> DEFINE_SET = new HashSet<>();
	private static final Map<Channel, WebSocketEndPointDefine> channelPointHolder = new HashMap<>();

	public static WebSocketEndPointDefine getEndPoint(String uri) {
		for (WebSocketEndPointDefine point : DEFINE_SET) {
			if (UriParameterUtil.match(uri, point.getUri())) {
				return point;
			}
		}
		return null;
	}

	/**
	 * 添加扫描到的endPointDefine
	 *
	 * @param define
	 */
	public static void addEndPoint(WebSocketEndPointDefine define) {
		Assert.notNull(define);
		DEFINE_SET.add(define);
	}

	public static WebSocketEndPointDefine getEndPoint(Channel channel) {
		return channelPointHolder.get(channel);
	}

	/**
	 * 添加 根据连接选择的 endPoint
	 *
	 * @param channel
	 * @param define
	 */
	public static void addEndPoint(Channel channel, WebSocketEndPointDefine define) {
		Assert.notNull(channel);
		Assert.notNull(define);
		channelPointHolder.put(channel, define);
	}

	public static Object[] getParam(WebSocketFrame frame, Method method, Channel channel, ApplicationContext context) {

		Class<?>[] parameterTypes = method.getParameterTypes();
		Object[] parameters = new Object[parameterTypes.length];
		Annotation[][] annotationArray = method.getParameterAnnotations();
		WebSocketUrlParam urlParam = WebSocketParamHolder.getChannelParams(channel);

		for (int i = 0; i < parameterTypes.length; i++) {
			Object parameter;
			Class<?> type = parameterTypes[i];
			Annotation[] annotation = annotationArray[i];
			if (annotation == null || annotation.length == 0) {
				// 如果该参数类型是基础类型，则需要加RouterParam注解
				if (PrimitiveTypeUtil.isPriType(type)) {
					LOGGER.warn("Must specify a @Param annotation for primitive type parameter in method={}",
							method.getName());
					continue;
				}
				if (WebSocketFrame.class.isAssignableFrom(type)) {
					parameters[i] = frame;
				}
				if (Channel.class.isAssignableFrom(type)) {
					parameters[i] = channel;
				}
			} else if (annotation[0] instanceof Param) {
				Param param = (Param) annotation[0];
				String name = param.key();
				String attr = WebSocketParamHolder.attr(channel, name);
				parameters[i] = attr;
			} else {
				parameters[i] = null;
			}
		}

		return parameters;
	}

	public static void scan(String pkc) {
		Assert.notBlank(pkc);
		Set<Class<?>> classSet = ClassScaner.scanPackageByAnnotation(pkc, ServerEndPoint.class);
		if (CollectionUtil.isNotEmpty(classSet)) {
			for (Class<?> aClass : classSet) {
				ServerEndPoint serverEndPoint = AnnotationUtil.getAnnotation(aClass, ServerEndPoint.class);
				String path = serverEndPoint.value();
				if (StrUtil.isBlank(path)) {
					continue;
				}
				WebSocketEndPointDefine pointDefine = new WebSocketEndPointDefine(path);
				Method[] methods = aClass.getMethods();
				for (Method method : methods) {
					WebSocketEventHandler annotation = AnnotationUtil.getAnnotation(method,
							WebSocketEventHandler.class);
					if (ObjectUtil.isNotNull(annotation)) {
						WebSocketEvent event = annotation.value();
						if (event.equals(WebSocketEvent.ON_CLOSE)) {
							pointDefine.setExceptionMethod(method);
						}
						if (event.equals(WebSocketEvent.ON_ERROR)) {
							pointDefine.setCloseMethod(method);
						}
						if (event.equals(WebSocketEvent.ON_MESSAGE)) {
							pointDefine.setMsgMethod(method);
						}
						if (event.equals(WebSocketEvent.ON_OPEN)) {
							pointDefine.setOpenMethod(method);
						}
					}
				}

				EndPointHelper.addEndPoint(pointDefine);
			}
		}
	}
}
