package cn.geekelmon.cluster.node;

import cn.geeklemon.core.context.annotation.Value;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 11:34
 * Modified by : kavingu
 */
public class ClusterRootConfig implements ServiceNode {
    @Value(name = "lemon.cluster.rootPath", defaultValue = "/geeklemon-cluster")
    private String rootPath;
    @Value(name = "lemon.cluster.clusterHost", defaultValue = "127.0.0.1")
    private String clusterHost;
    @Value(name = "lemon.cluster.clusterPort", defaultValue = "9999")
    private Integer clusterPort;
    @Value(name = "lemon.data.zookeeper.address")
    private String zookeeperAddress;

    public String getRootPath() {
        return StrUtil.isBlank(rootPath) ? "/geeklemon-cluster" : rootPath;
    }

    @Override
    public String getHost() {
        return clusterHost;
    }

    @Override
    public Integer getPort() {
        return clusterPort;
    }

    @Override
    public String getPath() {
        return getRootPath();
    }

    public String getZookeeperAddress() {
        return zookeeperAddress;
    }

    public void setZookeeperAddress(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public void setClusterHost(String clusterHost) {
        this.clusterHost = clusterHost;
    }

    public void setClusterPort(Integer clusterPort) {
        this.clusterPort = clusterPort;
    }

    @Override
    public String id() {
        return getPath();
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
