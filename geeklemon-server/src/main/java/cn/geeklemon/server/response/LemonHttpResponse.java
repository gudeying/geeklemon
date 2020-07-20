package cn.geeklemon.server.response;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geeklemon.server.response.writer.DefaultCustomWriter;
import cn.geeklemon.server.response.writer.DefaultFileResWriter;
import cn.geeklemon.server.response.writer.HttpOutputStream;
import cn.geeklemon.server.response.writer.HttpResponseWriter;
import cn.geeklemon.server.response.writer.HttpWriter;
import cn.geeklemon.server.response.writer.WriteMode;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/29 11:05 Modified by : kavingu
 */
public class LemonHttpResponse implements cn.geeklemon.server.response.HttpResponse {
	private static final Logger LOGGER = LoggerFactory.getLogger(cn.geeklemon.server.response.HttpResponse.class);
	private HttpResponseStatus status;
	private Set<Cookie> cookies;
	private Map<CharSequence, Object> headers;

	private HttpResponseWriter writer;

	private ChannelHandlerContext context;

	public Map<CharSequence, Object> headers() {
		return headers;
	}

	public HttpResponseStatus getStatus() {
		return status;
	}

	@Deprecated
	public Set<Cookie> getCookies() {
		return cookies;
	}

	/**
	 * 建议使用addCookie(String name, String value)比较简单
	 * 
	 * @param cookies cookieSet
	 */
	@Deprecated
	public void setCookies(Set<Cookie> cookies) {
		this.cookies = cookies;
	}

	@Override
	public HttpResponse setStatus(HttpResponseStatus status) {
		this.status = status;
		return this;
	}

	@Override
	public HttpResponse addCookie(String name, String value) {
		DefaultCookie defaultCookie = new DefaultCookie(name, value);
		// **netty的文档中，0立即过期，Long.MAX_VALUE为浏览器退出删除，并不是负数，负数也是立即删除
		defaultCookie.setMaxAge(Long.MIN_VALUE);
		defaultCookie.setPath("/");
		addCookie(defaultCookie);
		return this;
	}

	@Override
	public HttpResponse addCookie(String name, String value, long maxAge) {
		DefaultCookie defaultCookie = new DefaultCookie(name, value);
		defaultCookie.setMaxAge(maxAge);
		defaultCookie.setPath("/");
		addCookie(defaultCookie);
		return this;
	}

	@Override
	public HttpResponseStatus status() {
		return status;
	}

	@Override
	public Set<Cookie> cookies() {
		return cookies;
	}

	private void addCookie(Cookie cookie) {
		Assert.notNull(cookie);
		if (ObjectUtil.isNotNull(cookies)) {
			cookies.remove(cookie);// 由于equals被重写，name相同不会添加，因此值不会改变
			cookies.add(cookie);
			return;
		}
		cookies = new HashSet<>();
		cookies.add(cookie);

	}

	@Override
	public HttpResponse addHeader(CharSequence headerName, Object value) {
		if (HttpHeaderNames.CONTENT_LENGTH.equals(headerName)) {
			LOGGER.error("不允许定义 content-length ");
			return this;
		}
		if (StrUtil.isBlank(headerName)) {
			return this;
		}
		if (headers == null) {
			headers = new HashMap<>();
			headers.put(headerName, value);
			return this;
		}
		headers.put(headerName, value);
		return this;
	}

	@Override
	public HttpResponseWriter getWriter(WriteMode mode) {
		if (writer != null) {
			return writer;
		}
		if (mode == null) {
			this.writer = new HttpWriter(new ByteArrayOutputStream(), this.context);
			return writer;
		}
		switch (mode) {
		case CUSTOM:
			this.writer = new DefaultCustomWriter(context);
			return writer;
		case FILE:
			this.writer = new DefaultFileResWriter(context, this);
			return writer;
		}
		return writer;
	}

	@Override
	public void complete() {
		if (writer != null) {
			writer.complete();
		}
		this.writer = null;
		this.context = null;
	}

	public ChannelHandlerContext getContext() {
		return context;
	}

	public void setContext(ChannelHandlerContext context) {
		this.context = context;
	}

	@Override
	public HttpWriter getPrintWriter() {
		this.writer = new HttpWriter(new ByteArrayOutputStream(), context);
		return (HttpWriter) writer;
	}

	@Override
	public OutputStream getOutputStream() {
		this.writer = new HttpOutputStream(context);
		return (OutputStream) writer;
	}

	public Writer getOutWriter() {
		return new PrintWriter(getOutputStream());
	}
}
