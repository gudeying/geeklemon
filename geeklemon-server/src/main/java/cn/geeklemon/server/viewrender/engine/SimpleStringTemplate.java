package cn.geeklemon.server.viewrender.engine;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import java.util.regex.Pattern;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;

/**
 * default template ，do nothing with file string
 */
public class SimpleStringTemplate implements Template {

	private String content;
	private Pattern regex = Pattern.compile("\\$\\{([^}]*)\\}");

	public SimpleStringTemplate(String content) {
		if (StrUtil.isNullOrUndefined(content)) {
			this.content = "";
		} else {
			this.content = content;
		}
	}

	@Override
	public void render(Map<String, Object> bindingMap, Writer writer) {

	}

	@Override
	public void render(Map<String, Object> bindingMap, OutputStream out) {

	}

	@Override
	public void render(Map<String, Object> bindingMap, File file) {

	}

	@Override
	public String render(Map<String, Object> bindingMap) {

		return content;
	}
}
