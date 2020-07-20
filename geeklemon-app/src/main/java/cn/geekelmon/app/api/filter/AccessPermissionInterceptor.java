package cn.geekelmon.app.api.filter;

import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.server.intercepter.WebAppInterceptor;
import cn.geeklemon.server.intercepter.WebInterceptor;
import cn.geeklemon.server.request.HttpRequest;
import cn.geeklemon.server.viewrender.ModelAndView;
import cn.hutool.core.util.StrUtil;

@Bean
@WebAppInterceptor("/manager/**")
public class AccessPermissionInterceptor implements WebInterceptor {

    @Override
    public boolean preHandle(HttpRequest request) throws Exception {
        String key = request.header("AccessToken");
        String pString = request.getParameter("permission");
        if ("force".equals(pString)) {
            // 为了测试
            return true;
        }
        return !StrUtil.isBlank(key);
    }

    @Override
    public void postHandle(HttpRequest request, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpRequest request, Exception exception) throws Exception {

    }

}
