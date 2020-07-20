package cn.geekelmon.example.ioc.cache;

import cn.geekelmon.cache.annotation.EnableCache;
import cn.geekelmon.example.ioc.cache.demo.Service;
import cn.geeklemon.core.context.annotation.GeekLemonApplication;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.context.support.LemonApplication;
import cn.hutool.cache.GlobalPruneTimer;
import cn.hutool.core.thread.ThreadUtil;

@GeekLemonApplication
@EnableCache
public class Test {

    public static void main(String[] args) {
        ApplicationContext context = LemonApplication.run(Test.class);
        Service service = context.getBean(Service.class);
        String test = service.test();
        System.out.println("第一次的值： " + test);
        ThreadUtil.sleep(1000);
        System.out.println("第二次的值： " + service.test());
        GlobalPruneTimer.INSTANCE.shutdown();
    }
}
