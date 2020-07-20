package cn.geekelmon.example.ioc.server;

import cn.geeklemon.core.context.annotation.GeekLemonApplication;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.context.support.LemonApplication;
import cn.geeklemon.core.util.ResourceUtils;
import cn.geeklemon.server.auto.WebApplication;

import java.io.File;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/23 16:26
 * Modified by : kavingu
 */
@GeekLemonApplication
@WebApplication
public class ServerMain {
    public static void main(String[] args) {
        ResourceUtils.addResource("E:" + File.separator + "public");
        ApplicationContext context = LemonApplication.run(ServerMain.class);
    }
}
