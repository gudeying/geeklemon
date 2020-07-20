package cn.geekelmon.cluster.node;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 11:03
 * Modified by : kavingu
 */
public interface ServiceNode {

    /**
     * ip地址
     *
     * @return
     */
    String getHost();

    /**
     * 端口
     *
     * @return
     */
    Integer getPort();

    /**
     * 节点的路径
     *
     * @return
     */
    String getPath();

    default byte[] getData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("host", getHost());
        jsonObject.put("port", getPort());
        return StrUtil.utf8Bytes(jsonObject.toJSONString());
    }

    /**
     * id
     * @return
     */
    String id();

}
