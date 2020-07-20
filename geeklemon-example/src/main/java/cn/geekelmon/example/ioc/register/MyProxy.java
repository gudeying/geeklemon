package cn.geekelmon.example.ioc.register;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2020/1/10 9:00
 * Modified by : kavingu
 */
public class MyProxy implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        return "代理";
    }
}
