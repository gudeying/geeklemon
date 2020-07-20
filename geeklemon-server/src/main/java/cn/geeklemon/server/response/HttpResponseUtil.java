package cn.geeklemon.server.response;

import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.server.TemporaryDataHolder;
import cn.geeklemon.server.common.RenderType;
import cn.geeklemon.server.context.WebContext;
import cn.geeklemon.server.controller.ControllerDefine;
import cn.geeklemon.server.viewrender.ModelAndView;
import cn.geeklemon.server.viewrender.ViewEngineConfig;
import cn.geeklemon.server.viewrender.engine.SimpleFileToStringEngine;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.template.Engine;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateException;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * HttpRenderUtil
 *
 * @author houyi.wh
 * @date 2017-10-20
 */
public class HttpResponseUtil {

	public static final Logger LOGGER = LoggerFactory.getLogger(HttpResponseUtil.class);

	private static final String EMPTY_CONTENT = "";

	private HttpResponseUtil() {

	}

	/**
	 * response输出
	 *
	 * @param content
	 *            内容
	 * @param renderType
	 *            返回类型
	 * @return 响应对象
	 */
	public static FullHttpResponse render(Object content, RenderType renderType, HttpResponseStatus status) {
		if (ObjectUtil.isNull(content)) {
			content = "";
		}
		RenderType type = renderType != null ? renderType : RenderType.JSON;
		if (type == RenderType.JSON) {
			content = JSONUtil.toJsonStr(content);
		}
		if (type == RenderType.XML) {
			content = JSONUtil.toXmlStr(JSONUtil.parse(content));
		}
		HttpResponseStatus httpResponseStatus = ObjectUtil.isNull(status) ? HttpResponseStatus.OK : status;
		byte[] bytes = HttpResponseUtil.getBytes(content);
		ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus, byteBuf);

		response.headers().add(HttpHeaderNames.CONTENT_TYPE, type.getContentType());
		response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
		return response;
	}

	/**
	 * 404NotFoundResponse
	 *
	 * @return 响应对象
	 */
	public static FullHttpResponse getNotFoundResponse() {
		String content = DefaultHttpPage.NOT_FOUND;
		return render(content, RenderType.HTML, HttpResponseStatus.NOT_FOUND);
	}

	/**
	 * 404NotFoundResponse
	 *
	 * @return 响应对象
	 */
	public static FullHttpResponse getForbiddenResponse() {
		String content = DefaultHttpPage.FORBIDDEN;
		return render(content, RenderType.HTML, HttpResponseStatus.FORBIDDEN);
	}

	public static FullHttpResponse getNoServiceResponse() {
		String content = DefaultHttpPage.NO_SERVICE;
		return render(content, RenderType.HTML, HttpResponseStatus.NOT_FOUND);
	}

	/**
	 * ServerErrorResponse
	 *
	 * @return 响应对象
	 */
	public static FullHttpResponse getServerErrorResponse() {
		JSONObject object = new JSONObject();
		object.put("code", 500);
		object.put("message", "Server Internal Error!");
		return render(object, RenderType.JSON, HttpResponseStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * ErrorResponse
	 *
	 * @param errorMessage
	 *            错误信息
	 * @return 响应对象
	 */
	public static FullHttpResponse getErrorResponse(String errorMessage) {
		JSONObject object = new JSONObject();
		object.put("code", 300);
		object.put("message", errorMessage);
		return render(object, RenderType.JSON, HttpResponseStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 转换byte
	 *
	 * @param content
	 *            内容
	 * @return 响应对象
	 */
	private static byte[] getBytes(Object content) {
		if (content == null) {
			return EMPTY_CONTENT.getBytes(CharsetUtil.UTF_8);
		}
		String data = content.toString();
		data = (data == null || data.trim().length() == 0) ? EMPTY_CONTENT : data;
		return data.getBytes(CharsetUtil.UTF_8);
	}

	public static FullHttpResponse viewRender(Object content, ControllerDefine controllerDefine,
			ApplicationContext applicationContext) {
		Engine templateViewEngine = applicationContext.getBean(Engine.class);
		ViewEngineConfig config = applicationContext.getBean(ViewEngineConfig.class);
		if (ObjectUtil.isNull(templateViewEngine)) {
			try {
				templateViewEngine = TemplateUtil.createEngine();
			} catch (TemplateException e) {
				// LOGGER.warn("未配置模板引擎，将使用简单的文件转换");
				templateViewEngine = new SimpleFileToStringEngine(config.getTemplatePath());
			}
		}

		RenderType renderType = controllerDefine.getRenderType();
		Method method = controllerDefine.getMethod();
		Class<?> returnType = method.getReturnType();
		if (renderType == RenderType.VIEW || renderType == RenderType.HTML) {
			FullHttpResponse response = null;
			if (returnType.equals(ModelAndView.class) && content instanceof ModelAndView) {
				ModelAndView modelAndView = (ModelAndView) content;
				String fileName = config.getPrefix() + modelAndView.getTemplateName() + config.getSuffix();
				Template template = templateViewEngine.getTemplate(fileName);
				byte[] bytes = HttpResponseUtil.getBytes(template.render(modelAndView.getModel()));
				ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
				HttpResponse lemonResponse = TemporaryDataHolder.loadLemonResponse();
				HttpResponseStatus status = lemonResponse.status();
				if (ObjectUtil.isNull(status)) {
					status = HttpResponseStatus.OK;
				}
				response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);

				/**
				 * 写用户的cookie
				 */
				Set<Cookie> cookies = lemonResponse.cookies();
				if (ObjectUtil.isNotNull(cookies)) {
					for (Cookie cookie : cookies) {
						TemporaryDataHolder.storeCookie(cookie);
					}
				}
				response.headers().add(HttpHeaderNames.CONTENT_TYPE, renderType.getContentType());
				response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
			} else if (returnType.equals(String.class)) {
				if (ObjectUtil.isNull(content)) {
					LOGGER.warn("{} return null value", method);
					return render("", controllerDefine.getRenderType(), HttpResponseStatus.OK);
				}
				String strContent = (String) content;
				String templateFile = config.getPrefix() + strContent + config.getSuffix();
				Template template = templateViewEngine.getTemplate(templateFile);
				String result = template.render(new HashMap<>());
				byte[] bytes = HttpResponseUtil.getBytes(result);
				ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);

				HttpResponse lemonResponse = TemporaryDataHolder.loadLemonResponse();
				HttpResponseStatus status = lemonResponse.status();
				if (ObjectUtil.isNull(status)) {
					status = HttpResponseStatus.OK;
				}
				response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);

				/**
				 * 写用户的cookie
				 */
				Set<Cookie> cookies = lemonResponse.cookies();
				if (ObjectUtil.isNotNull(cookies)) {
					for (Cookie cookie : cookies) {
						TemporaryDataHolder.storeCookie(cookie);
					}
				}
				response.headers().add(HttpHeaderNames.CONTENT_TYPE, renderType.getContentType());
				response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
			}

			return response;
		}
		return render(content, controllerDefine.getRenderType(), HttpResponseStatus.OK);
	}

	public static FullHttpResponse getResponseByModeAndView(ModelAndView modelAndView, WebContext webContext) {
		LemonHttpResponse lemonResponse = (LemonHttpResponse) TemporaryDataHolder.loadLemonResponse();
		HttpResponseStatus status = lemonResponse.status();
		if (ObjectUtil.isNull(status)) {
			status = HttpResponseStatus.OK;
		}

		/**
		 * 写用户的cookie
		 */
		Set<Cookie> cookies = lemonResponse.cookies();
		if (ObjectUtil.isNotNull(cookies)) {
			for (Cookie cookie : cookies) {
				TemporaryDataHolder.storeCookie(cookie);
			}
		}
		FullHttpResponse response = null;
		RenderType renderType = modelAndView.getRenderType();
		if (renderType == RenderType.VIEW || renderType == RenderType.HTML) {
			Engine engine = webContext.getEngine();
			String templatePath = webContext.getViewEngineConfig().getTemplatePath();
			String templateName = modelAndView.getTemplateName();
			String templateFile = templatePath + templateName;
			Template template = engine.getTemplate(templateFile);
			String render = template.render(modelAndView.getModel());
			ByteBuf byteBuf = Unpooled.wrappedBuffer(getBytes(render));
			response = new DefaultFullHttpResponse(HTTP_1_1, status, byteBuf);
			response.headers().add(HttpHeaderNames.CONTENT_TYPE, renderType.getContentType());
			response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
			return response;
		}

		Object content = modelAndView.getContent() == null ? "" : modelAndView.getContent();
		String result = "";
		if (renderType == RenderType.JSON) {
			Object json = JSONObject.toJSON(content);
			// result = JSONUtil.toJsonStr(content);
			result = json.toString();
		}
		if (renderType == RenderType.XML) {
			result = JSONUtil.toXmlStr(JSONUtil.parse(content));
		}
		/* text类型直接使用toStrng */
		ByteBuf byteBuf = Unpooled.wrappedBuffer(getBytes(result));

		response = new DefaultFullHttpResponse(HTTP_1_1, status, byteBuf);
		response.headers().add(HttpHeaderNames.CONTENT_TYPE, renderType.getContentType());
		response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
		return response;
	}

	public static boolean isClose(HttpRequest request) {
		if (request == null) {
			request = TemporaryDataHolder.loadHttpRequest();
		}
		return request.headers().contains(HttpHeaderNames.CONNECTION, "close", true)
				|| (request.protocolVersion().equals(HttpVersion.HTTP_1_0)
						&& !request.headers().contains(HttpHeaderNames.CONNECTION, "keep-alive", true));
	}

	public static void send(io.netty.handler.codec.http.HttpResponse response, ChannelHandlerContext context,
			boolean close) {
		if (close) {
			context.writeAndFlush(response, context.newPromise()).addListener(ChannelFutureListener.CLOSE);
		} else {
			context.writeAndFlush(response);
		}
	}

	public static void addHeaderAndCookie(io.netty.handler.codec.http.HttpResponse response) {
		HttpResponse lemonResponse = TemporaryDataHolder.loadLemonResponse();
		Map<CharSequence, Object> headers = lemonResponse.headers();
		if (MapUtil.isNotEmpty(headers)) {
			headers.forEach((key, value) -> {
				response.headers().add(key, value);
			});
		}
		Set<Cookie> cookies = lemonResponse.cookies();
		if (CollectionUtil.isNotEmpty(cookies)) {
			for (Cookie cookie : cookies) {
				response.headers().add(HttpHeaderNames.SET_COOKIE,
						io.netty.handler.codec.http.cookie.ServerCookieEncoder.STRICT.encode(cookie));
			}

		}
	}

	public static HttpResponseStatus getStatus() {
		HttpResponse response = TemporaryDataHolder.loadLemonResponse();
		HttpResponseStatus status = null;
		if (response.status() == null) {
			status = HttpResponseStatus.OK;
		} else {
			status = response.status();
		}
		return status;
	}

}
