package cn.geekelmon.example.ioc.cluster.slave.controller;

import cn.geekelmon.example.ioc.server.entity.TestUser;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/14 13:42
 * Modified by : kavingu
 */
@Controller
public class IndexController {
    @Autowired
    private TestUser user;

    @Mapping(path = "/")
    public String hello() {
        return "hello ! ";
    }

    @Mapping(path = "/test")
    public TestUser user() {
        return user;
    }

}
