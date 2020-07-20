package cn.geeklemon.server.multipart;

import java.io.File;
import java.io.IOException;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/23 15:21 Modified by : kavingu
 */
public interface MultiPartFile {
	public String getName();

	public boolean transferTo(File dest) throws IOException;

	public long length();
}
