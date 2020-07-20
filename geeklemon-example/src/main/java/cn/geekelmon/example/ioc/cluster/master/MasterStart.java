package cn.geekelmon.example.ioc.cluster.master;

import cn.geekelmon.cluster.bootstrap.ClusterBootstrap;
import cn.geekelmon.cluster.node.ClusterRootConfig;
import cn.geekelmon.example.ioc.server.entity.TestUser;
import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.core.context.annotation.GeekLemonApplication;
import cn.geeklemon.core.context.annotation.Import;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.context.support.LemonApplication;
import cn.hutool.core.date.DateUtil;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 14:57
 * Modified by : kavingu
 */
@GeekLemonApplication
@Import({ClusterRootConfig.class, ClusterBootstrap.class})
public class MasterStart {
    public static void main(String[] args) {
        ApplicationContext context = LemonApplication.run(MasterStart.class);

    }
}
