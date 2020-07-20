package cn.geekelmon.app.api.entity;

import java.io.Serializable;

/**
 * 一些请求的结果封装
 */
public class ActionResponse implements Serializable {
    private int status;
    private boolean success;
    private String msg;
    private String data;

    private String appendData;

    public ActionResponse(int status, boolean success, String msg) {
        this.status = status;
        this.success = success;
        this.msg = msg;
    }

    public ActionResponse(String msg) {
        success = false;
        this.msg = msg;
    }

    public ActionResponse() {
        success = true;
        msg = "success";
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAppendData() {
        return appendData;
    }

    public void setAppendData(String appendData) {
        this.appendData = appendData;
    }
}
