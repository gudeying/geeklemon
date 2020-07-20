package cn.geeklemon.core.context.support.external;

/**
 * 外部需要实现的接口，可以使用 register 注册 {@link cn.geeklemon.core.bean.factory.BeanInitializationDefinition}
 * 可以使用相关自动注入但是该bean不会注入到容器中，结合{@link cn.geeklemon.core.bean.factory.InitializingBean}实现注册功能
 * 执行时间在普通bean执行初始化之前
 */
public interface BeanInitRegisterExternal {
    void setRegister(BeanInitDefineRegister register);

    void register();
}