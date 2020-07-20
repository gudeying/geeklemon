package cn.geekelmon.app.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Base64;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;

public class ImgToBase64 {

	/**
	 * 
	 * @param file
	 *            resource地址，可以是绝对路径或者classpath路径
	 * @return
	 */
	public static String getImgData(String file) {
		InputStream inputStream = null;
		try {
			inputStream = ResourceUtil.getStream(file);
			FastByteArrayOutputStream read = IoUtil.read(inputStream);
			byte[] data = read.toByteArray();
			inputStream.read(data);
			byte[] encode = Base64.getEncoder().encode(data);
			String result = new String(encode, Charset.forName("utf-8"));
			return result;
			// return StrUtil.str(encode, Charset.defaultCharset());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
			}
		}

	}

	/**
	 * jar包中获取不到File，请使用{@link #getImgData(String)}}
	 * 
	 * 
	 * hutool的编码做了处理，jar包模式下运行解析的结果不一样，无法使用
	 * 
	 * @param imgFile
	 * @return
	 */
	public static String getImgData(File imgFile) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(imgFile);
			byte[] data = IoUtil.read(inputStream).toByteArray();
			return Base64Encoder.encode(data);
		} catch (Exception e) {
			return "";
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
			}
		}
	}

}
