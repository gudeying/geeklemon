package cn.geekelmon.data.context;

import cn.geeklemon.core.context.support.chain.FactoryBean;

/**
 */
public class MapperFactoryBean implements FactoryBean {
    private Class<?> srcClass;
    private Object result;

    public MapperFactoryBean(Class<?> srcClass, Object result) {
        this.srcClass = srcClass;
        this.result = result;
    }

    public Class<?> getSrcClass() {
        return srcClass;
    }

    public MapperFactoryBean setSrcClass(Class<?> srcClass) {
        this.srcClass = srcClass;
        return this;
    }

    public Object getResult() {
        return result;
    }

    public MapperFactoryBean setResult(Object result) {
        this.result = result;
        return this;
    }

    @Override
    public Object getObject() throws Exception {
        return result;
    }

    @Override
    public Class<?> getObjectType() {
        return srcClass;
    }
}
