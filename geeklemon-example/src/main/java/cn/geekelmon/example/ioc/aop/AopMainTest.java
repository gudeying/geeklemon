package cn.geekelmon.example.ioc.aop;

import cn.geeklemon.core.aop.support.*;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 */
public class AopMainTest {
    public static void main(String[] args) {

        List<VoidPoint> list = new LinkedList<>();
        list.add(new VoidPoint() {
            @Override
            public void proceed(Object o, Method method, Object[] args, ProxyChain chain, MethodProxy methodProxy, PointResult pointResult) throws Throwable {
                System.out.println("[1] before");

                Object proceed = chain.proceed(o, method, args, methodProxy, null);
                System.out.println("[1]获取返回结果");

                System.out.println("[1] after");
            }
        });
        list.add(new VoidPoint() {
            @Override
            public void proceed(Object o, Method method, Object[] args, ProxyChain chain, MethodProxy methodProxy, PointResult pointResult) throws Throwable {
                System.out.println("[2] before");

                Object proceed = chain.proceed(o, method, args, methodProxy, new PointResult() {
                    @Override
                    public Object getResult() {
                        System.out.println("[2]拦截结果");
                        return "[2]return result";
                    }

                    @Override
                    public boolean forceReturn() {
                        return true;
                    }
                });
                System.out.println("[2] after");
            }
        });
        list.add(new VoidPoint() {
            @Override
            public void proceed(Object o, Method method, Object[] args, ProxyChain chain, MethodProxy methodProxy, PointResult pointResult) throws Throwable {
                System.out.println("[3]");
            }
        });
        Object proxy = LProxyFactory.create().getProxy(new SayHello(), list, null);
        SayHello sayHello = (SayHello) proxy;
        String string = sayHello.toString();
        System.out.println("最终结果" + string);
    }


    static class SayHello {

        @Override
        public String toString() {
            return "hello cglib !";
        }
    }
}
