package cn.geekelmon.app.api.entity;

import cn.geekelmon.data.annotation.LColumn;
import cn.geekelmon.data.annotation.LTable;

@LTable("gallery")
public class GalleryInfo {

	@LColumn("id")
	private Integer id;
	@LColumn("src")
	private String origionUrl;
	@LColumn("comsrc")
	private String shortCutUrl;
	@LColumn("user_open_id")
	private String userOpenId;
	@LColumn("des")
	private String content;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrigionUrl() {
		return origionUrl;
	}

	public void setOrigionUrl(String origionUrl) {
		this.origionUrl = origionUrl;
	}

	public String getShortCutUrl() {
		return shortCutUrl;
	}

	public void setShortCutUrl(String shortCutUrl) {
		this.shortCutUrl = shortCutUrl;
	}

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
