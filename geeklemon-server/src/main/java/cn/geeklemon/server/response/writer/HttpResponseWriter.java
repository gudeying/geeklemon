package cn.geeklemon.server.response.writer;

public interface HttpResponseWriter {
	HttpResponseWriter writeData(byte[] data) throws Exception;

	/**
	 * 资源关闭等操作，最后一步执行
	 */
	void complete();
}
