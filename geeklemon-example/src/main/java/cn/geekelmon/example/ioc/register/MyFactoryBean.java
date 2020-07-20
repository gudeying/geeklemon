package cn.geekelmon.example.ioc.register;

import cn.geeklemon.core.context.support.chain.FactoryBean;

/**
 */
public class MyFactoryBean implements FactoryBean {


    @Override
    public Object getObject() throws Exception {
        return new MyService();
    }

    @Override
    public Class<?> getObjectType() {
        return Service.class;
    }
}
