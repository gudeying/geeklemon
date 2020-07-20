package cn.geeklemon.server.request;

import cn.geeklemon.server.TemporaryDataHolder;
import cn.geeklemon.server.session.DefaultHttpSession;
import cn.geeklemon.server.session.HttpSession;
import cn.geeklemon.server.session.SessionHelper;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/29 9:45 channelAttribute可以存储值，为了扩展这里使用了map保存数据
 * Modified by : kavingu
 */
public class LemonHttpRequest implements HttpRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LemonHttpRequest.class);

    private io.netty.handler.codec.http.HttpRequest request;
    private Map<String, List<String>> requestParameterMap;
    private HttpSession session;

    private String uri;
    private SessionHelper sessionHelper = SessionHelper.getInstance();

    public LemonHttpRequest() {
        request = TemporaryDataHolder.loadHttpRequest();
        Assert.notNull(request);
        this.uri = request.uri();
        this.session = sessionHelper.getSession();

    }

    private void parseParamMap() {
        Map<String, List<String>> map = HttpRequestUtil.getParameterMap(request);
        requestParameterMap = new HashMap<String, List<String>>();
        map.forEach((key, listValue) -> {
            requestParameterMap.put(key, listValue);
        });
    }

    @Override
    public HttpSession session() {
        if (ObjectUtil.isNull(session)) {
            this.session = sessionHelper.getSession();
        }
        return session;
    }

    @Override
    public HttpSession session(boolean create) {
        HttpSession session = session();
        if (ObjectUtil.isNull(session) && create) {
            return addSession();
        }
        return session;
    }

    private HttpSession addSession() {
        String sessionId = StrUtil.uuid();
        HttpSession session = new DefaultHttpSession(sessionId, sessionHelper);
        sessionHelper.addSession(session);
        this.session = session;
        return session;
    }

    @Override
    public String getParameter(String paramName) {
        if (requestParameterMap == null) {
            parseParamMap();
        }
        if (requestParameterMap.containsKey(paramName)) {
            return requestParameterMap.get(paramName).get(0);
        }
        return null;
    }

    public Set<Cookie> getCookies() {
        Set<Cookie> cookies = new HashSet<>();
        if (request != null) {
            String value = request.headers().get(HttpHeaderNames.COOKIE);
            if (value != null) {
                cookies = ServerCookieDecoder.STRICT.decode(value);
            }
        }
        return cookies;
    }

    @Override
    public Cookie[] Cookies() {
        return ArrayUtil.toArray(getCookies(), Cookie.class);
    }

    @Override
    public String sessionId() {
        return session == null ? null : session.sessionId();
    }

    @Override
    public String URI() {
        return uri;
    }

    @Override
    public HttpMethod requestMethod() {
        return request.method();
    }

    @Override
    public String header(String name) {
        HttpHeaders headers = request.headers();
        return headers.get(name);
    }

    @Override
    public List<Map.Entry<String, String>> headers() {
        HttpHeaders headers = request.headers();
        return ObjectUtil.isNull(headers) ? new LinkedList<>() : headers.entries();
    }

    @Override
    public Map<String, List<String>> parameterMap() {
        if (requestParameterMap == null) {
            parseParamMap();
        }
        return requestParameterMap;
    }

    public void addParam(String name, String value) {
        Assert.notNull(name);
        Assert.notNull(value);
        List<String> list = new ArrayList<>();
        list.add(value);
        if (requestParameterMap == null) {
            parseParamMap();
        }
        requestParameterMap.put(name, list);

    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }
}
