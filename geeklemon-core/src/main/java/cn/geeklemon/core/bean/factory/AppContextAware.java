package cn.geeklemon.core.bean.factory;

import cn.geeklemon.core.context.support.ApplicationContext;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/12 9:58
 * Modified by : kavingu
 */
public interface AppContextAware {
    void setContext(ApplicationContext context);
}
