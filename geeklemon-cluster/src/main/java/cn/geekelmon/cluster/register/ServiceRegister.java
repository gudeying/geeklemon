package cn.geekelmon.cluster.register;

import cn.geekelmon.cluster.node.ClusterRootConfig;
import cn.geekelmon.cluster.node.ServiceNode;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 11:24
 * Modified by : kavingu
 */
public interface ServiceRegister {
    void register(ServiceNode serviceNode);
}
