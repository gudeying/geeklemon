package cn.geekelmon.example.ioc.iocdemo.test;

import cn.geekelmon.example.ioc.server.service.UserDao;
import cn.geekelmon.example.ioc.server.service.UserService;
import cn.geeklemon.core.bean.factory.InitializingBean;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.context.support.LemonApplication;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/11 16:36
 * Modified by : kavingu
 */
//@GeekLemonApplication
public class IocAppTest implements InitializingBean {
    @Autowired
    private UserService service;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private UserDao userDao;

    public static void main(String[] args) {
        ApplicationContext context = LemonApplication.run(IocAppTest.class);

    }

    public void sayHello() {
        System.out.println("say hello");
        System.out.println("service实例：" + service);
        System.out.println("使用service执行 【getUserByName】方法：");
        System.out.println(service.getUserByName("hhh"));
    }

    @Override
    public void afterPropsSet() {
        System.out.println(this.getClass().getName() + "执行 afterPropsSet");
        System.out.println("将执行 sayHello()");
        sayHello();
    }

//    void testContext() {
//        System.out.println(context.getBean(UserService.class).getUserByName(""));
//    }
}
