package cn.geeklemon.server.response.writer;

import io.netty.channel.ChannelHandlerContext;

public class DefaultCustomWriter implements CustomWriter {
	private ChannelHandlerContext context;

	public DefaultCustomWriter(ChannelHandlerContext context) {
		this.context = context;
	}

	@Override
	public void complete() {

	}

	@Override
	public ChannelHandlerContext getChannelHandlerContext() {
		return context;
	}

}
