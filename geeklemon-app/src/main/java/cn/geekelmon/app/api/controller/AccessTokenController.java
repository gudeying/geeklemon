package cn.geekelmon.app.api.controller;

import cn.geekelmon.app.api.entity.ApiEntity;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;

@Controller
public class AccessTokenController {

    @Mapping(path = "/app/info/token/validate/fail")
    public ApiEntity tokenValidateFailed() {
        return new ApiEntity("token校验失败");
    }

}
