package cn.geeklemon.server.filter;

import cn.geeklemon.server.request.HttpRequest;
import cn.geeklemon.server.response.HttpResponse;
import cn.geeklemon.server.viewrender.ModelAndView;

/**
 * 过滤器处于业务处理的最上层，包括文件处理
 */
public interface WebFilter {
    /**
     * 如果调用了 HttpResponse的任何write方法回写了数据，一定要返回false。
     * <br/>
     * 你可以使用httpRequest.setUri()方法改变uri
     *
     * @param request  request
     * @param response response
     * @return 是否交给下一个过滤器进行处理。如果是最后一个过滤器，返回 true将进入后续处理，比如 resource、controllerDispacher
     */
    boolean accept(HttpRequest request, HttpResponse response);
}
