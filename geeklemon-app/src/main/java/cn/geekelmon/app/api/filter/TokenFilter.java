package cn.geekelmon.app.api.filter;

import cn.geekelmon.app.api.entity.ApiEntity;
import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.server.common.RenderType;
import cn.geeklemon.server.filter.WebAppFilter;
import cn.geeklemon.server.filter.WebFilter;
import cn.geeklemon.server.request.HttpRequest;
import cn.geeklemon.server.response.HttpResponse;
import cn.geeklemon.server.viewrender.ModelAndView;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

import java.util.Map;

/**
 * 对写操作的请求验证token
 */
@Bean
@WebAppFilter
public class TokenFilter implements WebFilter {

    @Override
    public boolean accept(HttpRequest request, HttpResponse response) {
        String uri = request.URI();
        if (uri.trim().startsWith("/write")) {
            //TODO token校验
            response.addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
            ApiEntity<Object> entity = new ApiEntity<>("token校验失败");
            String jsonStr = JSONUtil.toJsonStr(entity);
            response.getPrintWriter().write(jsonStr);

            return false;
        }
        return true;
    }

}
