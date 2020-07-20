package cn.geekelmon.example.ioc.server.Controller;

import cn.geekelmon.example.ioc.server.entity.TestUser;
import cn.geekelmon.example.ioc.server.service.UserService;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.controller.annotation.Param;
import cn.geeklemon.server.request.HttpRequest;
import cn.geeklemon.server.response.HttpResponse;

import java.util.Date;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/23 17:22
 * Modified by : kavingu
 */
@Controller
public class UserController {
    @Autowired
    private UserService service;

    @Mapping(path = "/name")
    public String name() {
        return service.getUserByName("HHHHHH");
    }

    @Mapping(path = "/")
    public String index() {
        return "Hello World";
    }

    @Mapping(path = "/test")
    public String test(HttpRequest request, HttpResponse response) {
        String name = request.getParameter("name");
        System.out.println(name);

//        HttpSession session = request.session(true);
//        session.setAttribute("userName", "administrator");
//        Object userName = request.session().getAttribute("userName");
//        System.out.println(userName);
        return "test return";
    }

    @Mapping(path = "/user/{name}", CrossOrigin = true)
    public TestUser testPathVariable(@Param(key = "name") String name, HttpRequest request) {
        TestUser user = new TestUser();
        user.setAge(10);
        user.setBirth(new Date());
        user.setName(name);
        return user;
    }

    @Mapping(path = "/test/model")
    public TestUser testModel(TestUser user) {
        System.out.println(user);
        return user;
    }

}
