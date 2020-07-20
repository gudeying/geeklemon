package cn.geeklemon.server.session;

import cn.hutool.core.lang.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/12 13:44
 * Modified by : kavingu
 */
public class DefaultHttpSession implements HttpSession {
    /**
     * 标识唯一id
     */
    private String sessionId;
    private Long createTime;

    private long maxAge = 30 * 60 * 1000;//30分分钟转换为毫秒

    private SessionContext sessionContext;

    /**
     * 过期时间
     * 每次请求时都更新过期时间
     */
    private Long expireTime;

    /**
     * Session中存储的数据
     */
    private Map<String, Object> attributeMap;


    public DefaultHttpSession(String sessionId, SessionContext sessionContext) {
        Assert.notBlank(sessionId, "[DefaultSession] construct sessionId 不能为空");
        Assert.notNull(sessionContext);
        attributeMap = new HashMap<>();
        this.sessionId = sessionId;
        this.createTime = System.currentTimeMillis();
        this.sessionContext = sessionContext;
        this.expireTime = createTime + maxAge;
    }

    @Override
    public String sessionId() {
        return sessionId;
    }

    @Override
    public void setAttribute(String key, Object val) {
        Assert.notBlank(key, "session 的名称不能为空");
        attributeMap.put(key, val);
        /**
         * 同时更新sessionContext中的session
         */
        sessionContext.addOrUpdateSession(this);

    }

    @Override
    public Object getAttribute(String key) {
        return attributeMap.get(key);
    }

    @Override
    public void removeAttribute(String key) {
        attributeMap.remove(key);
        sessionContext.addOrUpdateSession(this);
    }

    public boolean expire() {
        return expireTime < System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultHttpSession that = (DefaultHttpSession) o;

        return sessionId != null ? sessionId.equals(that.sessionId) : that.sessionId == null;
    }

    @Override
    public int hashCode() {
        return sessionId != null ? sessionId.hashCode() : 0;
    }
}
