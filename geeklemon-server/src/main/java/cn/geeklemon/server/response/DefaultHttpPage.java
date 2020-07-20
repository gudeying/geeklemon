package cn.geeklemon.server.response;

import cn.hutool.core.util.StrUtil;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/23 12:45 Modified by : kavingu
 */
public final class DefaultHttpPage {

	private DefaultHttpPage() {

	}

	public static final String NOT_FOUND;
	public static final String NO_SERVICE;
	public static final String FORBIDDEN;

	static {

		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>").append(StrUtil.CRLF).append("<html lang=\"en\">").append(StrUtil.CRLF)
				.append("<head>").append(StrUtil.CRLF).append(StrUtil.TAB).append("<meta charset=\"UTF-8\">")
				.append(StrUtil.CRLF).append(StrUtil.TAB).append("<title>404 Not Found</title>").append(StrUtil.CRLF)
				.append("</head>").append(StrUtil.CRLF).append("<body>").append(StrUtil.CRLF).append(StrUtil.TAB)
				.append("<h1>").append(StrUtil.CRLF).append(StrUtil.TAB).append(StrUtil.TAB).append("Request not found")
				.append(StrUtil.CRLF).append(StrUtil.TAB).append("</h1>").append(StrUtil.CRLF).append("<br/>")
				.append("<br/>").append("<hr>").append("<h4>").append("GeekLemon 1.0").append("</h4>").append("<hr>")
				.append("</body>").append(StrUtil.CRLF).append("</html>");
		NOT_FOUND = sb.toString();
		sb = new StringBuilder();
		sb.append("<!DOCTYPE html>").append(StrUtil.CRLF).append("<html lang=\"en\">").append(StrUtil.CRLF)
				.append("<head>").append(StrUtil.CRLF).append(StrUtil.TAB).append("<meta charset=\"UTF-8\">")
				.append(StrUtil.CRLF).append(StrUtil.TAB).append("<title>Service Not Available</title>")
				.append(StrUtil.CRLF).append("</head>").append(StrUtil.CRLF).append("<body>").append(StrUtil.CRLF)
				.append(StrUtil.TAB).append("<h1>").append(StrUtil.CRLF).append(StrUtil.TAB).append(StrUtil.TAB)
				.append("No Service Found，Check  Status").append(StrUtil.CRLF).append(StrUtil.TAB).append("</h1>")
				.append(StrUtil.CRLF).append("<br/>").append("<br/>").append("<hr>").append("<h4>")
				.append("GeekLemon 1.0").append("</h4>").append("<hr>").append("</body>").append(StrUtil.CRLF)
				.append("</html>");
		NO_SERVICE = sb.toString();

		sb = new StringBuilder();
		sb.append("<!DOCTYPE html>").append(StrUtil.CRLF).append("<html lang=\"en\">").append(StrUtil.CRLF)
				.append("<head>").append(StrUtil.CRLF).append(StrUtil.TAB).append("<meta charset=\"UTF-8\">")
				.append(StrUtil.CRLF).append(StrUtil.TAB).append("<title>403 forbidden</title>").append(StrUtil.CRLF)
				.append("</head>").append(StrUtil.CRLF).append("<body>").append(StrUtil.CRLF).append(StrUtil.TAB)
				.append("<h1>").append(StrUtil.CRLF).append(StrUtil.TAB).append(StrUtil.TAB).append("Access Denied")
				.append(StrUtil.CRLF).append(StrUtil.TAB).append("</h1>").append(StrUtil.CRLF).append("<br/>")
				.append("<br/>").append("<hr>").append("<h4>").append("GeekLemon 1.0").append("</h4>").append("<hr>")
				.append("</body>").append(StrUtil.CRLF).append("</html>");
		FORBIDDEN = sb.toString();
	}
}