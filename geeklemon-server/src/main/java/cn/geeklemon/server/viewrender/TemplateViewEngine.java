package cn.geeklemon.server.viewrender;

import java.util.Map;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/10 14:59
 * Modified by : kavingu
 */
public interface TemplateViewEngine {
    String process(ModelAndView modelAndView);
    String process(String template, Map modelMap);
}
