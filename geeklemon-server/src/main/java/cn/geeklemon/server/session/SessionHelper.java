package cn.geeklemon.server.session;

import cn.geeklemon.server.TemporaryDataHolder;
import cn.geeklemon.server.cookie.CookieUtil;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 */
public class SessionHelper implements SessionContext {
    private static SessionHelper ourInstance = new SessionHelper();

    /**
     * 默认session过期时间：30分钟
     */
    private TimedCache<String, HttpSession> sessionMap = CacheUtil.newTimedCache(30 * 60 * 1000);

    public static SessionHelper getInstance() {
        return ourInstance;
    }

    private SessionHelper() {
        sessionMap.schedulePrune(10 * 100);//十秒执行一次检查是否过期
    }

    /**
     * @return nullAble
     */
    public HttpSession getSession() {
        String sessionId = CookieUtil.getSessionId(TemporaryDataHolder.loadHttpRequest());
        return getSession(sessionId);
    }

    @Override
    public void addSession(HttpSession session) {
        Assert.notNull(session, "[Add Session] session can not be bull !");
        Assert.notBlank(session.sessionId(), "[Add Session] sessionId can not be null or blank !");
        String sessionId = CookieUtil.getSessionId(TemporaryDataHolder.loadHttpRequest());
        if (StrUtil.isNotBlank(sessionId)) {
            sessionMap.put(sessionId, session);
            return;
        }
        sessionMap.put(session.sessionId(), session);
        TemporaryDataHolder.loadLemonResponse().addCookie("LEMONSESSIONID", session.sessionId());
    }

    @Override
    public HttpSession getSession(String sessionId) {
        HttpSession session = sessionMap.get(sessionId);
        if (session != null && !session.expire()) {
            return session;
        }
        return null;
    }

    @Override
    public void addOrUpdateSession(HttpSession session) {
        Assert.notNull(session);
        String s = session.sessionId();
        sessionMap.remove(s);
        sessionMap.put(session.sessionId(), session);
    }

}
