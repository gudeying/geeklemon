package cn.geeklemon.server.session;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/12 13:36
 * Modified by : kavingu
 * <p>
 */
public interface HttpSession {

    String sessionId();

    /**
     * 设置session
     *
     * @param key AttributeName
     * @param val AttributeValue
     */
    void setAttribute(String key, Object val);

    /**
     * 获取session
     *
     * @param key AttributeName
     * @return Attribute
     */
    Object getAttribute(String key);

    /**
     * 删除为key的session
     *
     * @param key AttributeName
     */
    void removeAttribute(String key);

    /**
     * 该session是否过期
     *
     * @return 是否过期
     */
    boolean expire();

}
