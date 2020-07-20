package cn.geeklemon.server.response.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import cn.geeklemon.server.response.HttpResponseUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpWriter extends PrintWriter implements HttpResponseWriter {
	private ByteArrayOutputStream outputStream;
	private ChannelHandlerContext context;

	public HttpWriter(ByteArrayOutputStream outputStream, ChannelHandlerContext context) {
		super(outputStream);
		this.outputStream = outputStream;
		this.context = context;
	}

	@Override
	public  HttpResponseWriter writeData(byte[] data) throws Exception {
		outputStream.write(data);
		outputStream.flush();
		return this;
	}

	public  HttpWriter writeData(String data) throws Exception {
		outputStream.write(data.getBytes());
		outputStream.flush();
		return this;
	}

	@Override
	public  void complete() {
		// 必须flush
		super.flush();
		if (outputStream.size() > 0) {

			HttpResponseStatus status = HttpResponseUtil.getStatus();

			DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
					Unpooled.wrappedBuffer(outputStream.toByteArray()));
			httpResponse.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(outputStream.size()));
			HttpResponseUtil.addHeaderAndCookie(httpResponse);
			ChannelFuture channelFuture =

					context.writeAndFlush(httpResponse, context.newPromise());

			channelFuture.addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {

				}
			});
		}
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpWriter.this.outputStream = null;
		HttpWriter.this.close();
		this.context = null;
	}

}
