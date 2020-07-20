package cn.geekelmon.example.ioc.data.entity;

import cn.geekelmon.data.annotation.LColumn;
import cn.geekelmon.data.annotation.LTable;

@LTable("test_info")
public class TestInfo {
	@LColumn("t_id")
	private int id;
	@LColumn("t_msg")
	private String msg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
