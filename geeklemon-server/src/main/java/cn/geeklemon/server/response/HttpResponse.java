package cn.geeklemon.server.response;

import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import cn.geeklemon.server.response.writer.HttpResponseWriter;
import cn.geeklemon.server.response.writer.HttpWriter;
import cn.geeklemon.server.response.writer.WriteMode;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;

public interface HttpResponse {
	/**
	 * response status
	 * 
	 * @param status
	 * @return
	 */
	HttpResponse setStatus(HttpResponseStatus status);

	HttpResponse addCookie(String name, String value);

	HttpResponse addCookie(String name, String value, long maxAge);

	HttpResponseStatus status();

	/**
	 * 该方法只返回用户在response中设置的cookie 访问全部cookie使用httpRequest
	 *
	 * @return
	 */
	Set<Cookie> cookies();

	/**
	 * 请不要添加 TRANSFER_ENCODING
	 * 
	 * @param headerName
	 * @param value
	 * @return
	 */
	HttpResponse addHeader(CharSequence headerName, Object value);

	/**
	 * 一次会话只能获取一个HttpResponseWriter
	 * 
	 * @param mode
	 * @return
	 */
	HttpResponseWriter getWriter(WriteMode mode);

	/**
	 * 适用于用户拼接字符串回写等少量内容<br/>
	 * <br/>
	 * HttpResponseWriter 一次会话只能获取一个HttpResponseWriter
	 * 最后将buffer中的内容根据length传输<br/>
	 * 所以不要添加transfer-encoding和content-length<br/>
	 * 
	 * @return
	 */
	HttpWriter getPrintWriter();

	/**
	 * 一次会话只能获取一个HttpResponseWriter<br/>
	 * 使用chunk传输，所以不要添加transfer-encoding
	 * 
	 * @return
	 */
	OutputStream getOutputStream();

	/**
	 * 资源销毁等工作，用户不必调用
	 */
	void complete();

	Map<CharSequence, Object> headers();

}
