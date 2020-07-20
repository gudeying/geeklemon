package cn.geekelmon.cluster.register;

import cn.geekelmon.cluster.node.ClusterRootConfig;
import cn.geekelmon.cluster.node.ServiceNode;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 11:25
 * Modified by : kavingu
 */
public class ZookeeperServiceRegister implements ServiceRegister {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceRegister.class);
    private CuratorFramework zkClient;

    public ZookeeperServiceRegister(CuratorFramework zkClient) {
        Assert.notNull(zkClient);
        this.zkClient = zkClient;
    }

    @Override
    public void register(ServiceNode serviceNode) {
        try {
            if (zkClient.checkExists().forPath(serviceNode.getPath()) == null) {
                // 创建临时节点
                zkClient.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                        .forPath(serviceNode.getPath(), serviceNode.getData());
            }
        } catch (Exception e) {
            LOGGER.error("register slave error with slave={},cause:", serviceNode.getPath(), e);
        }

    }

    public void remove(ServiceNode serviceNode) {
        try {
            zkClient.delete()
                    .deletingChildrenIfNeeded()//如果只是删除叶子节点，不需要这个
                    .forPath(serviceNode.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
