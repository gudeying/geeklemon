package cn.geekelmon.app.api.entity;

import java.io.Serializable;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/11/5 12:46
 * Modified by : kavingu
 */
public class ApiEntity<T> implements Serializable {
    private String Msg;
    /**
     * 0 :正常，使用int
     */
    private int Code;
    private T Data;

    /**
     * 成功
     *
     * @param data
     */
    public ApiEntity(T data) {
        Data = data;
        this.Msg = "success";
        this.Code = 0;
    }

    public ApiEntity(String msg, int code, T data) {
        Msg = msg;
        Code = code;
        Data = data;
    }

    /**
     * 失败的请求
     *
     * @param msg
     */
    public ApiEntity(String msg) {
        Msg = msg;
        this.Code = 1;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }
}
