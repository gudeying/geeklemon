package cn.geeklemon.server.response.writer;

import io.netty.channel.ChannelHandlerContext;

public interface CustomWriter extends HttpResponseWriter {
	@Override
	default HttpResponseWriter writeData(byte[] data) throws Exception {
		getChannelHandlerContext().writeAndFlush(data);
		return this;
	}

	/**
	 * ChannelHandlerContext 直接回写response
	 * 
	 * @return
	 */
	ChannelHandlerContext getChannelHandlerContext();
}
