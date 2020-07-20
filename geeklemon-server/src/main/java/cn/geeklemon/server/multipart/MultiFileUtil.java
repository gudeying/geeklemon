package cn.geeklemon.server.multipart;

import cn.geeklemon.server.multipart.annotation.MultiFile;
import cn.hutool.core.util.StrUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.*;

import javax.xml.bind.ValidationException;

/**
 * @author : Kavin Gu Project Name : redant Description :
 * @version : ${VERSION} 2019/2/19 14:35 Modified by : kavingu
 */
public class MultiFileUtil {
	private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE); // Disk
																												// if
																												// size
																												// exceed
	private static HttpPostRequestDecoder decoder;

	public static LemonMultiFile getMultiFileObj(FullHttpRequest request, MultiFile annotation)
			throws ValidationException {
		if (decoder != null) {
			decoder.destroy();
		}
		String name = annotation.key();
		if (StrUtil.isBlank(name)) {
			return null;
		}
		decoder = new HttpPostRequestDecoder(factory, request);
		if (!decoder.isMultipart()) {
			throw new RuntimeException("不是multipart请求");
		}
		decoder.offer(request);
		FileUpload upload = readHttpDataChunkByChunk(decoder, name);
		if (upload != null) {
			return new LemonMultiFile(upload);
		} else if (annotation.required()) {
			throw new ValidationException("参数" + name + "不能省略!");
		}
		return null;
	}

	private static FileUpload readHttpDataChunkByChunk(HttpPostRequestDecoder decoder, String name) {
		InterfaceHttpData data = null;
		try {
			while (decoder.hasNext()) {
				// 就是httpPost的参数{"a":"a值","b":"b值"}
				data = decoder.next();
				if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
					String paramName = data.getName();
					if (name.equals(paramName)) {
						return (FileUpload) data;
					}
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}
}
