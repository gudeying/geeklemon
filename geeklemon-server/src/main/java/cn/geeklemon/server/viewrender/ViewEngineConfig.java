package cn.geeklemon.server.viewrender;

import java.nio.charset.Charset;

import cn.geeklemon.core.context.annotation.Value;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Engine;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateUtil;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/10/8 8:43 Modified by : kavingu
 */
public class ViewEngineConfig {
	@Value(name = "template.charset", defaultValue = "utf-8")
	private String charset = "utf-8";
	@Value(name = "template.path", defaultValue = "template/")
	private String templatePath = "template/";
	/**
	 * 后缀
	 */
	@Value(name = "template.suffix", defaultValue = ".html")
	private String suffix = ".html";
	/**
	 * 前缀
	 */
	@Value(name = "template.prefix", defaultValue = "")
	private String prefix = "";

	// @Bean
	public Engine engine() {
		return TemplateUtil
				.createEngine(new TemplateConfig(this.Charset(), this.path(), TemplateConfig.ResourceMode.FILE));
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public Charset Charset() {
		/**
		 * 为空返回utf-8
		 */
		return CharsetUtil.charset(this.charset);
	}

	public String path() {
		return StrUtil.blankToDefault(this.templatePath, ClassUtil.getClassPath() + "template/");
	}

	public String getSuffix() {
		return suffix;
	}

	public ViewEngineConfig setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}

	public ViewEngineConfig setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public String getPrefix() {
		return prefix;
	}
}
