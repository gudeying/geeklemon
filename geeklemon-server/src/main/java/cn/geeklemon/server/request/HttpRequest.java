package cn.geeklemon.server.request;

import cn.geeklemon.server.session.HttpSession;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.List;
import java.util.Map;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/20 16:29
 * Modified by : kavingu
 */
public interface HttpRequest {
    /**
     * 获取session<br/>
     * 请注意NullPointException
     *
     * @return
     */
    HttpSession session();

    /**
     * 获取session，如果crate，未获取到则尝试注册一个session
     *
     * @param create
     * @return
     */
    HttpSession session(boolean create);

    /**
     * 根据名称获取参数
     *
     * @param paramName
     * @return
     */
    String getParameter(String paramName);

    /**
     * 所有的cookie
     *
     * @return
     */
    Cookie[] Cookies();

    /**
     * sessionId,NullPointException
     *
     * @return
     */
    String sessionId();

    /**
     * request请求连接字符串
     *
     * @return
     */
    String URI();

    /**
     * 请求的方法
     *
     * @return HttpMethod
     * @see HttpMethod
     */
    HttpMethod requestMethod();

    /**
     * 根据名称获取header
     * NullPointException
     *
     * @param name
     * @return
     */
    String header(String name);

    /**
     * 所有的header或者一个空的List
     *
     * @return
     */
    List<Map.Entry<String, String>> headers();

    /**
     * 所有参数
     *
     * @return
     */
    Map<String, List<String>> parameterMap();

    void setUri(String uri);
}
