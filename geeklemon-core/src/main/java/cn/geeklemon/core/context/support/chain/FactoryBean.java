package cn.geeklemon.core.context.support.chain;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/12 12:35
 * Modified by : kavingu
 */
public interface FactoryBean<T> {
    T getObject() throws Exception;

    Class<?> getObjectType();
}
