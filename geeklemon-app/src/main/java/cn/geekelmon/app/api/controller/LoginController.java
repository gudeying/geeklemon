package cn.geekelmon.app.api.controller;

import java.util.HashMap;
import java.util.Map;

import cn.geeklemon.server.common.RenderType;
import cn.geeklemon.server.common.RequestMethod;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.controller.annotation.Param;
import cn.geeklemon.server.multipart.MultiPartFile;
import cn.geeklemon.server.multipart.annotation.MultiFile;
import cn.geeklemon.server.request.HttpRequest;
import cn.geeklemon.server.session.HttpSession;
import cn.hutool.core.util.ObjectUtil;

@Controller
public class LoginController {

    @Mapping(path = "/login")
    public Map<String, Object> login(@Param(key = "name") String userName, @Param(key = "password") String password,
                                     @MultiFile(key = "logo", required = false) MultiPartFile file, HttpRequest request) {

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "登陆成功");
        result.put("userName", userName);
        String fileName = file.getName();
        long fileSize = file.length();
        System.out.println(fileName + ":" + fileSize);
        request.session(true).setAttribute("loginUser", userName);

        return result;
    }

    @Mapping(path = "/login/result")
    public String testLoginResult(HttpRequest request) {
        HttpSession session = request.session();
        Object loginUser = null;
        loginUser = session.getAttribute("loginUser");
        System.out.println(loginUser);
        return (String) loginUser;
    }

    @Mapping(path = "/login/page", renderType = RenderType.HTML, requestMethod = RequestMethod.GET)
    public String notLogin() {
        return "login";
    }

    @Mapping(path = "/logout")
    public String logOut(HttpRequest request) {
        String msg = "未登录";
        HttpSession session = request.session();
        if (ObjectUtil.isNotNull(session)) {
            session.removeAttribute("loginUser");
            msg = "已退出登陆！";
        }
        return msg;
    }

}
