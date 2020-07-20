package cn.geeklemon.server.viewrender;

import cn.geeklemon.server.common.RenderType;
import cn.geeklemon.server.controller.annotation.Param;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class ModelAndView {
	/**
	 * RenderType为HTML或者VIEW使用的html文件名称
	 */
	private String templateName;
	/**
	 * RenderType为HTML或者VIEW渲染模板所需的数据
	 */
	private Map<String, Object> model;
	private RenderType renderType;
	/**
	 * 不使用网页渲染，直接返回XML、JSON或者TEXT使用的内容
	 */
	private Object content;

	public ModelAndView() {
	}

	ModelAndView(String templateName, Map<String, Object> model) {
		this.model = model;
		this.templateName = templateName;
	}

	public String getTemplateName() {
		return templateName;
	}

	/**
	 * 包含前缀和后缀的文件名称
	 * 
	 * @param templateName
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Map<String, Object> getModel() {
		return model;
	}

	public void setModel(Map<String, Object> model) {
		this.model = model;
	}

	public void addAttribute(String name, Object value) {
		if (this.model == null) {
			this.model = new HashMap<>();
			this.model.put(name, value);
		} else {
			this.model.put(name, value);
		}
	}

	public void removeAttribute(String name) {
		if (this.model == null) {
			return;
		}
		this.model.remove(name);
	}

	public Object getAttribute(String name) {
		if (this.model == null) {
			return null;
		}
		return this.model.get(name);
	}

	public RenderType getRenderType() {
		return renderType;
	}

	public void setRenderType(RenderType renderType) {
		this.renderType = renderType;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "ModelAndView [templateName=" + templateName + ", renderType=" + renderType + ", content=" + content
				+ "]";
	}

}