package cn.geeklemon.core.aop;


import cn.geeklemon.core.aop.support.PointResult;
import cn.geeklemon.core.aop.support.ProxyChain;
import cn.geeklemon.core.aop.support.VoidPoint;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class LStringAopProxyPoint implements VoidPoint {
    private Method method;
    private Object filter;
    private AopType aopType;

    public LStringAopProxyPoint(Method method, Object filter, AopType aopType) {
        this.method = method;
        this.filter = filter;
        this.aopType = aopType;
    }

    @Override
    public void proceed(Object o, Method method, Object[] args, ProxyChain chain, MethodProxy methodProxy, PointResult pointResult) throws Throwable {
        if (CollectionUtil.isNotEmpty(AspectContext.getStringVoidPoint(method))) {
            PointDefine define = new PointDefine(o, method, args, pointResult);
            switch (aopType) {
                case BEFORE:
                    ReflectUtil.invoke(filter, this.method, define);
                    chain.proceed(o, method, args, methodProxy, pointResult);
                    return;
                case AFTER:
                    chain.proceed(o, method, args, methodProxy, pointResult);
                    ReflectUtil.invoke(filter, this.method, define);
                    return;
                case AROUND:
                    ReflectUtil.invoke(filter, this.method, define);
                    Object proceed = chain.proceed(o, method, args, methodProxy, pointResult);
                    define.setResult(proceed);
                    ReflectUtil.invoke(filter, this.method, define);
            }
        } else {
            chain.proceed(o, method, args, methodProxy, pointResult);
        }
    }
}
