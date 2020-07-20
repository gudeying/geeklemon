package cn.geekelmon.cluster.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import cn.geekelmon.cluster.node.ClusterRootConfig;
import cn.geekelmon.cluster.node.ServerServiceNode;
import cn.geekelmon.cluster.node.ServiceNode;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import io.netty.util.CharsetUtil;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 13:52
 * Modified by : kavingu
 */
public class ZookeeperServiceWatcher implements ServiceWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceWatcher.class);

    private CuratorFramework client;
    private ClusterRootConfig rootConfig;
    private final Map<String, ServiceNode> nodeMap = new HashMap<>();
    private ReentrantLock lock;
    private int slaveIndex = 0;

    public ZookeeperServiceWatcher(CuratorFramework zkClient, ClusterRootConfig rootConfig) {
        Assert.notNull(zkClient);
        this.client = zkClient;
        this.rootConfig = rootConfig;
        this.lock = new ReentrantLock();
    }

    @Override
    public void watch() {
        readAvailableNode();
        watchChange();
    }

    @Override
    public ServiceNode availableService() {
        if (client == null) {
            throw new IllegalArgumentException("zookeeper 客户端为空！");
        }
        lock.lock();
        try {
            if (nodeMap.size() == 0) {
                LOGGER.error("No available ServiceNode !");
                return null;
            }
            ServiceNode[] nodes = new ServerServiceNode[]{};
            nodes = nodeMap.values().toArray(nodes);
            // 通过CAS循环获取下一个可用服务
            if (slaveIndex >= nodes.length) {
                slaveIndex = 0;
            }
            int index = getServiceIndex(nodes.length);
            return nodes[slaveIndex++];
        } finally {
            lock.unlock();
        }
    }

    private int getServiceIndex(int length) {
        /*数值范围相同也会抛出异常*/
        return RandomUtil.randomInt(0, length);
    }

    private void readAvailableNode() {
        try {
            if (client.checkExists().forPath(rootConfig.getRootPath()) != null) {
                List<String> children = client.getChildren().forPath(rootConfig.getRootPath());
                for (String child : children) {
                    String childPath = rootConfig.getRootPath() + "/" + child;
                    byte[] data = client.getData().forPath(childPath);
                    ServiceNode node = JSON.parseObject(data, ServerServiceNode.class);
                    if (node != null) {
                        LOGGER.info("[Lemon Cluster Server] add slave={} to nodeMap when init", node);
                        nodeMap.put(node.id(), node);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("initNodeOnFirst error cause:", e);
        }
    }

    private void watchChange() {

        try {
            PathChildrenCache watcher = new PathChildrenCache(
                    client,
                    rootConfig.getRootPath(),
                    true
            );
            watcher.getListenable().addListener(new SlaveNodeWatcher());
            watcher.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        } catch (Exception e) {
            LOGGER.error("doWatch error cause:", e);
        }
    }

    private class SlaveNodeWatcher implements PathChildrenCacheListener {
        @Override
        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
            ChildData data = event.getData();
            if (data == null || data.getData() == null) {
                return;
            }
            ServiceNode node = JSON.parseObject(data.getData(), ServerServiceNode.class);
            if (node == null) {
                LOGGER.error("get a null slave with eventType={},path={},data={}", event.getType(), data.getPath(), data.getData());
            } else {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        nodeMap.put(node.id(), node);
                        LOGGER.info("CHILD_ADDED with path={},data={},current slave size={}", data.getPath(), new String(data.getData(), CharsetUtil.UTF_8), nodeMap.size());
                        break;
                    case CHILD_REMOVED:
                        nodeMap.remove(node.id());
                        LOGGER.info("CHILD_REMOVED with path={},data={},current slave size={}", data.getPath(), new String(data.getData(), CharsetUtil.UTF_8), nodeMap.size());
                        break;
                    case CHILD_UPDATED:
                        nodeMap.replace(node.id(), node);
                        LOGGER.info("CHILD_UPDATED with path={},data={},current slave size={}", data.getPath(), new String(data.getData(), CharsetUtil.UTF_8), nodeMap.size());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private class CallIndexReference {

    }

}
