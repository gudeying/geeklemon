package cn.geeklemon.server.request;

import cn.geeklemon.server.common.RequestMethod;
import cn.hutool.core.util.ObjectUtil;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/23 13:08 Modified by : kavingu
 */
public class RequestDefine {
    private String uri;
    private HttpMethod method;
    private HttpRequest httpRequest;

    private Map<String, List<String>> parameters;
    private Set<String> keySet;

    public RequestDefine(String uri, HttpMethod method, Map<String, List<String>> parameters, HttpRequest request) {
        this.uri = uri;
        this.method = method;
        this.parameters = parameters;
        this.httpRequest = request;
        keySet = parameters.keySet();

    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((keySet == null) ? 0 : keySet.hashCode());
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RequestDefine other = (RequestDefine) obj;
        if (keySet == null) {
            if (other.keySet != null)
                return false;
        } else if (!keySet.equals(other.keySet))
            return false;
        if (method == null) {
            if (other.method != null)
                return false;
        } else if (!method.equals(other.method))
            return false;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }

    public RequestMethod getMethodType() {
        try {
            /*自定义的method不全*/
            return RequestMethod.valueOf(method.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getPathMatcher() {
        return uri;
    }

    @Override
    public String toString() {
        return "RequestDefine{" + "uri='" + uri + '\'' + ", method=" + method + '}';
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    /**
     * 获取header
     *
     * @param name headerName
     * @return headerValue not null
     */
    public String header(String name) {

        if (ObjectUtil.isNull(httpRequest)) {
            return "";
        }
        return httpRequest.headers().get(name);
    }
}
