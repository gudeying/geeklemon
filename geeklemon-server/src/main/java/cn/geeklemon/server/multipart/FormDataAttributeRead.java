package cn.geeklemon.server.multipart;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

public class FormDataAttributeRead {
	private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE); // Disk
																												// if

	public static Map<String, List<String>> paramMap(FullHttpRequest request) {
		HttpPostRequestDecoder decoder = null;

		decoder = new HttpPostRequestDecoder(factory, request);
		if (!decoder.isMultipart()) {
			return null;
		}
		return getParamMap(decoder);
	}

	public static Map<String, List<String>> getParamMap(HttpPostRequestDecoder decoder) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		while (decoder.hasNext()) {
			try {
				InterfaceHttpData httpData = decoder.next();
				if (httpData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
					String name = httpData.getName();
					Attribute attribute = (Attribute) httpData;
					String value = attribute.getString(Charset.defaultCharset());
					if (map.get(name) == null) {
						List<String> list = new LinkedList<String>();
						list.add(value);
						map.put(name, list);
					} else {
						map.get(name).add(value);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}
}
