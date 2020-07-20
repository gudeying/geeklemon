package cn.geeklemon.core.bean.factory;

import cn.geeklemon.core.util.BeanNameUtil;
import cn.hutool.core.lang.Filter;

public interface BeanScannerFilter {
    /**
     * 方法可用于实现自定义的类扫描
     *
     * @param cls
     * @return
     */
    default Class<?> getBeanType(Class<?> cls) {
        return cls;
    }

    boolean accept(Class<?> cls);

    default String getName(Class<?> cls) {
        return BeanNameUtil.getBeanName(cls);
    }

    default boolean single(Class<?> cls) {
        return true;
    }
}
