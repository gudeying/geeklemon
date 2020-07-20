package cn.geeklemon.core.aop.proxy;

import cn.geeklemon.core.aop.AopMethodDefine;
import cn.geeklemon.core.aop.ProxyDefine;
import cn.geeklemon.core.bean.factory.BeanDefinition;
import net.sf.cglib.proxy.Enhancer;

import java.util.List;
import java.util.Set;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/18 17:31
 * Modified by : kavingu
 */
public class ProxyFactory {
    public static Object getProxy(Object bean, BeanDefinition beanDefinition, Set<AopMethodDefine> annotatedMethods, List<ProxyDefine> proxyDefines) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanDefinition.getSourceClass());
        enhancer.setCallback(new DefaultProxy(annotatedMethods, proxyDefines,bean));
//        for (AopMethodDefine annotatedMethod : annotatedMethods) {
//            System.out.println("方法：" + annotatedMethod.getMethod());
//            System.out.println("注解：" + annotatedMethod.getAnnotation());
//        }
//        System.out.println("获取代理" + beanDefinition.getSourceClass());
//        System.out.println("切面方法名称：");
//        for (ProxyDefine proxyDefine : proxyDefines) {
//            System.out.println(proxyDefine.getMethodName());
//        }
        return enhancer.create();
    }
}
