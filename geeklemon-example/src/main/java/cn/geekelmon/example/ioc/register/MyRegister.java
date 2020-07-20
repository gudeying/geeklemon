package cn.geekelmon.example.ioc.register;

import cn.geeklemon.core.bean.factory.BeanInitializationDefinition;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.support.external.BeanInitDefineRegister;
import cn.geeklemon.core.context.support.external.BeanInitRegisterExternal;
import cn.geeklemon.core.util.BeanNameUtil;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2020/1/9 16:58
 * Modified by : kavingu
 */
public class MyRegister implements BeanInitRegisterExternal {
    private BeanInitDefineRegister register;

    @Autowired
    private NeededService service;

    @Override
    public void setRegister(BeanInitDefineRegister register) {
        this.register = register;
    }

    @Override
    public void register() {
        service.serve();
        BeanInitializationDefinition definition = new BeanInitializationDefinition(BeanNameUtil.getBeanName(Service.class), new MyFactoryBean());
        register.register(definition);
    }
}
