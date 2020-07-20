package cn.geeklemon.server.response.writer;

public enum WriteMode {
	/**
	 * 传输文件 * <br/>
	 * {@link FileResWriter}
	 */
	FILE,
	/**
	 * 提供channelHandlerContext，由用户自定义 * <br/>
	 * {@link CustomWriter}
	 */
	CUSTOM;
}
