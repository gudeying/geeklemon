package cn.geeklemon.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

public class ObjUtil {
	public static byte[] getBytes(Object object) {
		if (object instanceof String) {
			return ((String) object).getBytes(Charset.defaultCharset());
		}
		if (object instanceof Serializable) {
			byte[] resultl;
			ByteArrayOutputStream byteArrayOutputStream = null;
			ObjectOutputStream objectOutputStream = null;
			try {
				byteArrayOutputStream = new ByteArrayOutputStream();
				objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
				objectOutputStream.writeObject(object);
				resultl = byteArrayOutputStream.toByteArray();
				return resultl;
			} catch (IOException e) {
				return "".getBytes();
			} finally {

				try {
					byteArrayOutputStream.close();
					objectOutputStream.close();
				} catch (Exception e) {
				}
			}

		}
		return "".getBytes();
	}
}
