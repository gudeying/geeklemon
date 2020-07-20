package cn.geekelmon.example.ioc.cluster.slave;

import cn.geekelmon.cluster.bootstrap.SlaveBootstrap;
import cn.geekelmon.cluster.node.ClusterRootConfig;
import cn.geekelmon.example.ioc.server.entity.TestUser;
import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.core.context.annotation.GeekLemonApplication;
import cn.geeklemon.core.context.annotation.Import;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.context.support.LemonApplication;
import cn.geeklemon.server.auto.ControllerBeanPostProcessor;
import cn.geeklemon.server.viewrender.ViewEngineConfig;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 15:12
 * Modified by : kavingu
 */
@GeekLemonApplication
@Import({SlaveBootstrap.class, ClusterRootConfig.class, ControllerBeanPostProcessor.class, ViewEngineConfig.class})
public class SlaveStart {
    private static Date birth = DateUtil.date();

    public static void main(String[] args) {
        ApplicationContext context = LemonApplication.run(SlaveStart.class);

    }

    @Bean(single = true)
    public TestUser user() {
        return new TestUser("kavin9999", birth, 23);
    }

}
