package cn.geekelmon.example.ioc.server.filter;

import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.server.handler.PreHandler;
import cn.geeklemon.server.handler.PreHandlerResult;
import cn.geeklemon.server.request.HttpRequest;
import cn.geeklemon.server.session.HttpSession;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.cookie.Cookie;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/29 14:58
 * Modified by : kavingu
 */
@Bean
@ChannelHandler.Sharable
@Deprecated
public class LoginFilter extends PreHandler {

    //    @Override
    public void accept(HttpRequest request) {
        LoginState loginState = new LoginState(request);
        if (loginState.isLogin()) {
            System.out.println(loginState.getUser() + " 已经登陆");
        } else {
            System.out.println("未登陆");
            String uri = request.URI().split("\\?")[0];
            if (uri.startsWith("/user/")) {
                this.sendRedirect("/login/page");
            }
        }
    }

    private class LoginState {
        private boolean login;
        private String user;

        LoginState(HttpRequest request) {

            HttpSession session = request.session();
            if (ObjectUtil.isNull(session)) {
                this.login = false;
                return;
            }
            String loginUser = (String) session.getAttribute("loginUser");
            if (StrUtil.isNotBlank(loginUser)) {
                this.login = true;
                this.user = loginUser;
            }
        }

        String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        boolean isLogin() {
            return login;
        }

        public void setLogin(boolean login) {
            this.login = login;
        }
    }

    @Override
    public PreHandlerResult handle(HttpRequest request) {
        // TODO Auto-generated method stub
        return null;
    }
}
