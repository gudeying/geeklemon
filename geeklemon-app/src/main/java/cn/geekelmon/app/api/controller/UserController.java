package cn.geekelmon.app.api.controller;

import cn.geekelmon.app.api.entity.ActionResponse;
import cn.geekelmon.app.api.entity.ApiEntity;
import cn.geekelmon.app.api.entity.LoginResponse;
import cn.geekelmon.app.api.entity.UserInfo;
import cn.geekelmon.app.api.service.ArticleService;
import cn.geekelmon.app.api.service.UserService;
import cn.geekelmon.app.api.service.mapper.UserInfoMapper;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.controller.annotation.Param;
import cn.geeklemon.server.request.HttpRequest;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/11/8 16:48
 * Modified by : kavingu
 */
@Controller
public class UserController {
    @Autowired
    private UserService service;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserInfoMapper mapper;


    @Mapping(path = "/app/info/user/detail")
    public ApiEntity<UserInfo> userInfo(@Param(key = "openId") String openId, HttpRequest request) {
        try {
            String loginUserOpenId = request.getParameter("loginUserOpenId");
            UserInfo user = service.user(openId);
            if (ObjectUtil.isNull(user)) {
                return new ApiEntity<>("未找到用户：" + openId);
            }
            if (StrUtil.isNotEmpty(loginUserOpenId)) {
                boolean followed = articleService.isFollowed(openId, loginUserOpenId);
                user.setFollowed(followed);
            }
            return new ApiEntity<>(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ApiEntity<>("内部出错");
    }

    @Mapping(path = "/app/info/user/token/validate")
    public ApiEntity<ActionResponse> validateToken(@Param(key = "token") String token) {
        //todo 判断token是否存在并且未过期
        return new ApiEntity<>(new ActionResponse());
    }


    @Mapping(path = "/app/info/user/login")
    public ApiEntity<LoginResponse> login(@Param(key = "name") String name, @Param(key = "password") String password) {
        try {

            UserInfo userByName = service.getUserByName(name);
            if (userByName == null) {
                return new ApiEntity<>("用户名或密码错误");
            }
            String md5Pas = SecureUtil.md5(password);
            String userMd5 = SecureUtil.md5(userByName.getPassword());//应该加密再传输
            if (md5Pas.equals(userMd5)) {

                //todo redis保存token
                String token = "token";
                LoginResponse response = new LoginResponse();
                response.setToken(token);
                response.setUserInfo(userByName);
                return new ApiEntity<>(response);
            }
            return new ApiEntity<>("用户名或密码错误");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ApiEntity<>("内部出错");
    }

    @Mapping(path = "/user/update")
    public ApiEntity<UserInfo> updateUser(UserInfo info) {
        String openId = info.getOpenId();
        if (StrUtil.isBlank(openId)) {
            return new ApiEntity<>("缺少openId");
        }
        mapper.update(info);
        return new ApiEntity<>(info);
    }

    @Mapping(path = "/user/info")
    public UserInfo userInfoByOpenId(@Param(key = "openId") String openId) {
        UserInfo userInfo = mapper.getUserByOpenId(openId);
        userInfo.setPassword("");
        return userInfo;
    }
}
