package cn.geeklemon.server.response.writer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import io.netty.channel.ChannelProgressiveFutureListener;

public interface FileResWriter extends HttpResponseWriter {
	/**
	 * 需要处理file的关闭 不会添加content-type
	 * 
	 * @param file
	 * @param listener
	 * @throws IOException
	 */
	void write(RandomAccessFile file, ChannelProgressiveFutureListener listener) throws IOException;

	/**
	 * 提供默认文件后缀名的content-type，如果在这之前调用response.addHeader()已经设置了content-type，则不会覆盖
	 * 
	 * @param file
	 * @throws IOException
	 */
	void write(File file) throws IOException;

	@Override
	default HttpResponseWriter writeData(byte[] data) throws Exception {
		throw new IllegalArgumentException("请写入File或者使用其他writer");
	}
}
