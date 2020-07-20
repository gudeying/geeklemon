package cn.geekelmon.cluster.context;

import cn.geekelmon.cluster.node.ServiceNode;

/**
 * 负责监听服务状态变化，获取服务
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 13:50
 * Modified by : kavingu
 */
public interface ServiceWatcher {
    /**
     * watch
     */
    void watch();

    /**
     * 获取一个可用的ServiceNode
     * @return ServiceNode
     */
    ServiceNode availableService();
}
