package cn.geekelmon.cache.key;

import java.lang.reflect.Method;
import java.util.Arrays;

public class DefaultCacheKey {
    private Method method;
    private Object[] args;
    private Object object;

    public DefaultCacheKey(Object object, Method method, Object[] args) {
        this.method = method;
        this.args = args;
        this.object = object;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultCacheKey that = (DefaultCacheKey) o;

        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        if (!Arrays.equals(args, that.args)) return false;
        return object != null ? object.equals(that.object) : that.object == null;
    }

    @Override
    public int hashCode() {
        int result = method != null ? method.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(args);
        result = 31 * result + (object != null ? object.hashCode() : 0);
        return result;
    }
}
