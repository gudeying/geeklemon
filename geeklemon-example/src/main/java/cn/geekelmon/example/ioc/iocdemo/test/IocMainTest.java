package cn.geekelmon.example.ioc.iocdemo.test;

import cn.geekelmon.example.ioc.server.entity.TestUser;
import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.core.context.annotation.GeekLemonApplication;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.context.support.LemonApplication;
import cn.hutool.core.date.DateUtil;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/14 12:33
 * Modified by : kavingu
 */
@GeekLemonApplication
public class IocMainTest {
    public static void main(String[] args) {
        ApplicationContext context = LemonApplication.run(IocMainTest.class);
        TestUser bean = context.getBean(TestUser.class);
        System.out.println(bean);

    }

    @Bean
    public TestUser user() {
        return new TestUser("kkk", DateUtil.date(), 22);
    }
}
