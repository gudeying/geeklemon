package cn.geeklemon.server.session;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/12 13:38
 * Modified by : kavingu
 */
public class SessionConfig {
    /**
     * 默认超时时间
     */
    private static final Long DEFAULT_SESSION_TIME_OUT = 60 * 60 * 1000L;

    /**
     * session超时时间
     */
    private Long sessionTimeOut;

    private HttpSession httpSessionImpl;

    private SessionContext sessionContext;

    public SessionContext getSessionContext() {
        return sessionContext;
    }

    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public static Long getDefaultSessionTimeOut() {
        return DEFAULT_SESSION_TIME_OUT;
    }

    public Long getSessionTimeOut() {
        return sessionTimeOut == null ? DEFAULT_SESSION_TIME_OUT : sessionTimeOut;
    }

    public void setSessionTimeOut(Long sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

    public HttpSession getHttpSessionImpl() {
        return httpSessionImpl;
    }

    public void setHttpSessionImpl(HttpSession httpSessionImpl) {
        this.httpSessionImpl = httpSessionImpl;
    }

}
