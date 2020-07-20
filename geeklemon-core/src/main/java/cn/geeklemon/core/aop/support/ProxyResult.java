package cn.geeklemon.core.aop.support;

import cn.hutool.core.util.ObjectUtil;

public interface ProxyResult {

    Object getResult();

    Throwable getException();

    default boolean exceptionCase() {
        return ObjectUtil.isNotNull(getException());
    }

}
