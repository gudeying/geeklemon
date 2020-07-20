package cn.geekelmon.app.api.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.geekelmon.app.api.entity.ArticlePreview;
import cn.geekelmon.app.api.util.FreeMarkTemplateUtil;
import cn.geekelmon.app.api.util.ImgToBase64;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.response.HttpResponse;
import cn.geeklemon.server.response.LemonHttpResponse;
import cn.geeklemon.server.response.writer.FileResWriter;
import cn.geeklemon.server.response.writer.WriteMode;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import freemarker.template.Template;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

@Controller
public class ResponseWriterTest {

	@Mapping(path = "/setup.cgi")
	public void file(HttpResponse response) {
		// File file = FileUtil.file("static/jquery.js");
		File file = FileUtil.file("/mysite/jquery.js");
		response.addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
		FileResWriter writer = (FileResWriter) response.getWriter(WriteMode.FILE);
		try {
			writer.write(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Mapping(path = "/elrekt.php")
	public void print(HttpResponse response) {
		response.addHeader(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
		response.getPrintWriter().write("你是卖报的小行家？");
	}

	@Mapping(path = "/index.action")
	public void chunk(HttpResponse response) {
		try {
			response.addHeader(HttpHeaderNames.CONTENT_TYPE, "text/js");
			OutputStream stream = response.getOutputStream();
			byte[] bu = new byte[10240];
			InputStream inputStream = ResourceUtil.getStream("static/jquery.js");
			int len = 0;
			while ((len = inputStream.read(bu)) > 0) {
				stream.write(bu, 0, len);
			}
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Mapping(path = "/excel")
	public void excel(HttpResponse response) {
		response.addHeader(HttpHeaderNames.CONTENT_TYPE, "application/vnd.ms-excel;charset=utf-8");
		response.addHeader("Content-Disposition", "attachment;filename=test.xls");

		OutputStream outputStream = response.getOutputStream();
		List<ArticlePreview> list = new LinkedList<>();
		for (int i = 0; i < 3000; i++) {
			ArticlePreview preview = new ArticlePreview();
			preview.setTitle("title" + i);
			list.add(preview);
		}
		ExcelWriter writer = ExcelUtil.getWriter();

		writer.write(list);
		writer.flush(outputStream);
		writer.close();
	}

	@Mapping(path = "/word")
	public void word(HttpResponse response) {
		LemonHttpResponse res = (LemonHttpResponse) response;
		response.addHeader(HttpHeaderNames.CONTENT_TYPE, "application/msword;charset=utf-8");
		response.addHeader("Content-Disposition", "attachment;filename=msg_info.doc");

		try {
			Template template = FreeMarkTemplateUtil.geTemplate("user_info.xml");
			String logoImg = "static/favicon.ico";
			String base64 = ImgToBase64.getImgData(logoImg);
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("location", "位置");
			model.put("phone", "123456789");
			model.put("sex", "男");
			model.put("logoImg", base64);
			model.put("firstName", "kavin");
			model.put("lastName", "gu");
			model.put("birth", DateUtil.date().toString());
			model.put("number", "12346575");

			template.process(model, res.getOutWriter());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
