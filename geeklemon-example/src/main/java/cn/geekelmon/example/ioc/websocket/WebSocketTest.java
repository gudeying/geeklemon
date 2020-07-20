package cn.geekelmon.example.ioc.websocket;

import cn.geeklemon.core.context.annotation.GeekLemonApplication;
import cn.geeklemon.core.context.support.LemonApplication;
import cn.geeklemon.server.auto.WebApplication;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/22 12:55
 * Modified by : kavingu
 */
@WebApplication
@GeekLemonApplication
public class WebSocketTest {
    public static void main(String[] args) {
        LemonApplication.run(WebSocketTest.class);
    }
}
