package cn.geekelmon.cluster.node;

import cn.hutool.core.lang.Assert;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 11:07
 * Modified by : kavingu
 */
public class StringIdDefine implements IdDefine<String> {
    private String id;

    public StringIdDefine(String id) {
        Assert.notBlank(id);
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
