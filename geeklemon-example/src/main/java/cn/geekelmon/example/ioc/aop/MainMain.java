package cn.geekelmon.example.ioc.aop;

import cn.geekelmon.example.ioc.aop.test.Service;
import cn.geeklemon.core.context.annotation.GeekLemonApplication;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.context.support.LemonApplication;

@GeekLemonApplication
public class MainMain {
    public static void main(String[] args) {
        ApplicationContext run = LemonApplication.run(MainMain.class);
        Service service = run.getBean(Service.class);

        service.test1("test1");
        service.test2("test2");
    }
}
