package cn.geeklemon.core.context.support.external;

import cn.geeklemon.core.bean.factory.BeanInitializationDefinition;

public interface BeanInitDefineRegister {
    void register(BeanInitializationDefinition definition);
}
