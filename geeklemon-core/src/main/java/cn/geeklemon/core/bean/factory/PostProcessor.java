package cn.geeklemon.core.bean.factory;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/16 9:54
 * Modified by : kavingu
 */
public interface PostProcessor {
    /**
     * @param bean           初始化并把属性注入好的bean
     * @param beanDefinition 保存了bean的定义
     *                       返回的bean将作为最终的实例，注意返回的bean必须是beanDefinition中sourceClass的子类，否则根据类型获取bean的时候会报错
     * @return bean
     */
    Object process(Object bean, BeanDefinition beanDefinition);
}
