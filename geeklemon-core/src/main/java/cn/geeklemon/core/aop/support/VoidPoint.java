package cn.geeklemon.core.aop.support;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 不拦截返回值的点
 */
public interface VoidPoint {
    /**
     * these parameters o,method,args,to give you some messages,but
     * do not invoke the method !
     */
    void proceed(Object target, Method method, Object[] args, ProxyChain chain, MethodProxy methodProxy, PointResult pointResult) throws Throwable;

    default int getSortCode() {
        return 0;
    }
}
