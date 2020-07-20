package cn.geeklemon.server.cookie;

import cn.geeklemon.server.TemporaryDataHolder;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

import java.util.HashSet;
import java.util.Set;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/30 9:32
 * Modified by : kavingu
 */
public class CookieUtil {
    /**
     * 解析http的cookie
     *
     * @param request
     * @return not null cookie set
     */
    public static Set<Cookie> getCookies(io.netty.handler.codec.http.HttpRequest request) {
        Set<Cookie> cookies = new HashSet<>();
        if (request != null) {
            String value = request.headers().get(HttpHeaderNames.COOKIE);
            if (value != null) {
                cookies = ServerCookieDecoder.STRICT.decode(value);
            }
        }
        return cookies;
    }

    /**
     * 获取cookie名称为“  LEMONSESSIONID ”的值作为sessionId<br/>
     *
     * @param request
     * @return not null sessionId
     */
    public static String getSessionId(HttpRequest request) {
        Set<Cookie> cookies = getCookies(request);
        String sessionId = "";
        for (Cookie cookie : cookies) {
            String name = cookie.name();
            sessionId = cookie.value();
            if (name.equals("LEMONSESSIONID")) {
                return sessionId;
            }
        }

        return sessionId;
    }
}
