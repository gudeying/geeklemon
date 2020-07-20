package cn.geekelmon.app.api.controller;

import java.util.HashMap;
import java.util.Map;

import cn.geeklemon.server.common.RenderType;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.response.HttpResponse;
import cn.geeklemon.server.response.writer.HttpWriter;
import cn.geeklemon.server.viewrender.ModelAndView;

/**
 */
@Controller

public class HtmlPageController {
	public HtmlPageController() {
		System.out.println("page init");
	}

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
		try {
			response.addHeader("Content-Type", "text/html");
			HttpWriter printWriter = (HttpWriter) response.getPrintWriter();
			printWriter.writeData("<!DOCTYPE html>").writeData("<html lang=\"en\">")
					.writeData("<head><meta charset=\"UTF-8\">").writeData(" <title>admin manager</title>")
					.writeData("</head><body><h3 style =\"text-align:center\" id=\"text\">管理员</h3></body></html>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
