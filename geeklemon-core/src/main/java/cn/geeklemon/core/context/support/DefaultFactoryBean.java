package cn.geeklemon.core.context.support;

import cn.geeklemon.core.context.support.chain.FactoryBean;

import java.lang.reflect.InvocationTargetException;

/**
 */
public class DefaultFactoryBean implements FactoryBean {
    private Class<?> srcClass;
    private Class<?> targetClass;

    public DefaultFactoryBean(Class<?> srcClass, Class<?> targetClass) {
        this.srcClass = srcClass;
        this.targetClass = targetClass;
    }

    @Override
    public Object getObject() throws Exception {
        return tryGetInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return srcClass;
    }

    private Object tryGetInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object object = targetClass.getConstructor(null).newInstance(null);
        return object;
    }
}
