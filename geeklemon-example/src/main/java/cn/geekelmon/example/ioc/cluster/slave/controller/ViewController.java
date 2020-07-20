package cn.geekelmon.example.ioc.cluster.slave.controller;

import cn.geeklemon.server.common.RenderType;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.request.HttpRequest;
import cn.hutool.core.util.StrUtil;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/14 15:57
 * Modified by : kavingu
 */
@Controller
public class ViewController {
    @Mapping(path = "/view", renderType = RenderType.HTML)
    public String test(HttpRequest request) {
        return "test";
    }
}
