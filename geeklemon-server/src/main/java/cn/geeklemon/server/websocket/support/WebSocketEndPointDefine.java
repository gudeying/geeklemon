package cn.geeklemon.server.websocket.support;

import cn.geeklemon.server.websocket.context.WebSocketEvent;
import cn.hutool.core.lang.Assert;

import java.lang.reflect.Method;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/22 10:10
 * Modified by : kavingu
 */
public class WebSocketEndPointDefine {
    private String uri;

    private Method openMethod;
    private Method msgMethod;
    private Method exceptionMethod;
    private Method closeMethod;

    public WebSocketEndPointDefine(String uri) {

        Assert.notBlank(uri);
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Method getOpenMethod() {
        return openMethod;
    }

    public void setOpenMethod(Method openMethod) {
        this.openMethod = openMethod;
    }

    public Method getMsgMethod() {
        return msgMethod;
    }

    public void setMsgMethod(Method msgMethod) {
        this.msgMethod = msgMethod;
    }

    public Method getExceptionMethod() {
        return exceptionMethod;
    }

    public void setExceptionMethod(Method exceptionMethod) {
        this.exceptionMethod = exceptionMethod;
    }

    public Method getCloseMethod() {
        return closeMethod;
    }

    public void setCloseMethod(Method closeMethod) {
        this.closeMethod = closeMethod;
    }
}
