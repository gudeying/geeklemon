package cn.geeklemon.server.session;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/30 9:29
 * Modified by : kavingu
 */
public interface SessionContext {
    /**
     * 向context中添加一个session
     *
     * @param session
     */
    void addSession( HttpSession session);

    /**
     * 根据sessionId获取session
     *
     * @param sessionId
     * @return session 可能为空
     */
    HttpSession getSession(String sessionId);

    /**
     * 同步session
     */
    void addOrUpdateSession(HttpSession session);
}
