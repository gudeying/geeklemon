package cn.geekelmon.app.api.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class FreeMarkTemplateUtil {
	private static Configuration templateConfig;
	static {
		templateConfig = new Configuration(Configuration.VERSION_2_3_28);
		templateConfig.setClassForTemplateLoading(FreeMarkTemplateUtil.class, "/template/word");
		templateConfig.setEncoding(Locale.CHINA, "utf-8");
		templateConfig.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_28));
		// 设置异常处理器,这样的话就可以${a.b.c.d}即使没有属性也不会出错
		templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);

	}

	public static Template geTemplate(String name) throws IOException {
		return templateConfig.getTemplate(name, "utf-8");
	}

	public static void main(String[] args) {
		try {
			Template template = geTemplate("user_info.xml");
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
			File dest = FileUtil.touch("H:/mycode/geeklemon/result.doc");
			FileWriter writer = new FileWriter(dest);
			template.process(model, writer);

		} catch (IOException | TemplateException e) {
			e.printStackTrace();
		}
	}

	public static void test() {
		try {
			Template template = geTemplate("index.html");
			Map<String, Object> model = new HashMap<>();
			model.put("content", "llalallala");
			File dest = FileUtil.touch("H:/mycode/geeklemon/testindex.html");
			FileWriter writer = new FileWriter(dest);
			template.process(model, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
