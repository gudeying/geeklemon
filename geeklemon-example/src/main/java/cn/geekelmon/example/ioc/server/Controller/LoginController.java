package cn.geekelmon.example.ioc.server.Controller;

import cn.geekelmon.example.ioc.server.service.UserService;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.server.common.RenderType;
import cn.geeklemon.server.common.RequestMethod;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.controller.annotation.Param;
import cn.geeklemon.server.request.HttpRequest;
import cn.geeklemon.server.session.HttpSession;
import cn.hutool.core.util.ObjectUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/30 9:49
 * Modified by : kavingu
 */
@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @Mapping(path = "/login")
    public Map<String, Object> login(@Param(key = "name") String userName, @Param(key = "password") String password, HttpRequest request) {
        String userByName = userService.getUserByName(userName);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "登陆成功");
        result.put("userName", userByName);

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
