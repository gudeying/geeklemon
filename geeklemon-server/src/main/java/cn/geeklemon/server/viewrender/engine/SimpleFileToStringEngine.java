package cn.geeklemon.server.viewrender.engine;

import cn.geeklemon.server.exception.TemplateFileNotFoundException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.template.Engine;
import cn.hutool.extra.template.Template;

import java.io.File;
import java.io.InputStream;

/**
 */
public class SimpleFileToStringEngine implements Engine {
	private String path;

	public SimpleFileToStringEngine(String path) {
		this.path = path;
	}

	@Override
	public Template getTemplate(String resource) {
		InputStream inputStream = ResourceUtil.getStream(resource);
		// InputStream inputStream =
		// this.getClass().getClassLoader().getResourceAsStream(path +
		// resource);
		if (inputStream == null) {
			File file = FileUtil.file(resource);
			inputStream = FileUtil.getInputStream(file);
		}
		String read = null;
		read = IoUtil.read(inputStream, CharsetUtil.UTF_8);
		if (ObjectUtil.isNull(read)) {
			throw new TemplateFileNotFoundException("template ： " + resource + " not found");
		}
		return new SimpleStringTemplate(read);
	}
}
