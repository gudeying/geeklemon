package cn.geekelmon.cluster.node;

import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 11:06
 * Modified by : kavingu
 */
public class ServerServiceNode implements ServiceNode {
    private String host;
    private Integer port;
    private String rootPath;

    public ServerServiceNode(String host, Integer port, String rootPath) {
        Assert.notBlank(host, "host 不能为空！");
        Assert.notNull(port, "port 不能为空！");
        this.host = host;
        this.port = port;
        this.rootPath = rootPath;
    }


    @Override
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }


    @Override
    public String getPath() {
        return rootPath + "/" + SecureUtil.md5(host + "&" + port);
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
