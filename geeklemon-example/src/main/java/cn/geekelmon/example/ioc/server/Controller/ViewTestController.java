package cn.geekelmon.example.ioc.server.Controller;

import cn.geeklemon.server.common.RenderType;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.request.HttpRequest;
import cn.geeklemon.server.response.HttpResponse;
import cn.geeklemon.server.viewrender.ModelAndView;
import cn.hutool.core.util.StrUtil;
import io.netty.handler.codec.http.HttpHeaderNames;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/10/10 15:19 Modified by : kavingu
 */
@Controller
public class ViewTestController {
	@Mapping(path = "/", renderType = RenderType.HTML)
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setTemplateName("index");
		modelAndView.setModel(new HashMap<>());
		return modelAndView;
	}

	@Mapping(path = "/admin-scripts.asp", renderType = RenderType.HTML)
	public String strIndex(ModelAndView modelAndView) {
		modelAndView.addAttribute("content", "Admin Access Get");
		return "leaf";
	}

	@Mapping(path = "/admin-page", renderType = RenderType.HTML)
	public String testMap(Map<String, Object> map) {
		map.put("content", "Access Denied");
		return "leaf";
	}

	@Mapping(path = "/manager/html")
	public void responseIndex(HttpResponse response) {
//		response.addHeader("Content-Type", "text/html");
//		response.write("<!DOCTYPE html>").write("<html lang=\"en\">").write("<head><meta charset=\"UTF-8\">")
//				.write(" <title>admin manager</title>")
//				.write("</head><body><h3 style =\"text-align:center\" id=\"text\">管理员</h3></body></html>");
	}
}
