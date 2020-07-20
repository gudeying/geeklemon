package cn.geeklemon.server.response.writer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;

import cn.geeklemon.core.util.ResourceUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;

public class DefaultFileResWriter implements FileResWriter {

	private ChannelHandlerContext context;
	private cn.geeklemon.server.response.HttpResponse lemonResponse;

	public DefaultFileResWriter(ChannelHandlerContext context,
			cn.geeklemon.server.response.HttpResponse lemonResponse) {
		this.context = context;
		this.lemonResponse = lemonResponse;
	}

	@Override
	public void complete() {
		this.context = null;
		this.lemonResponse = null;
	}

	@Override
	public void write(RandomAccessFile accessFile, ChannelProgressiveFutureListener listener) throws IOException {
		long fileLength = accessFile.length();

		HttpResponseStatus status = null;
		if (lemonResponse.status() == null) {
			status = HttpResponseStatus.OK;
		} else {
			status = lemonResponse.status();
		}

		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
		response.headers().add(HttpHeaderNames.CONTENT_LENGTH, fileLength);

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

		context.write(response);
		ChannelFuture sendFileFuture = context.writeAndFlush(
				new DefaultFileRegion(accessFile.getChannel(), 0, accessFile.length()),
				context.newProgressivePromise());
		sendFileFuture.addListener(listener);

	}

	@Override
	public void write(File file) throws IOException {
		boolean key = lemonResponse.headers().containsKey(HttpHeaderNames.CONTENT_TYPE);
		if (!key) {
			lemonResponse.addHeader(HttpHeaderNames.CONTENT_TYPE, getMimeType(file.getPath()));
		}
		RandomAccessFile accessFile = new RandomAccessFile(file, "r");
		ChannelProgressiveFutureListener listener = new ChannelProgressiveFutureListener() {

			@Override
			public void operationComplete(ChannelProgressiveFuture future) throws Exception {
				accessFile.close();
			}

			@Override
			public void operationProgressed(ChannelProgressiveFuture future, long progress, long total)
					throws Exception {
			}
		};
		write(accessFile, listener);
	}

	private String getMimeType(String string) {

		try {
			String shufix = string.substring(string.lastIndexOf(".") + 1, string.length());
			return ResourceUtils.getFIleContentType(shufix);
		} catch (Exception e) {
			return ResourceUtils.getFIleContentType("");
		}

	}

}
