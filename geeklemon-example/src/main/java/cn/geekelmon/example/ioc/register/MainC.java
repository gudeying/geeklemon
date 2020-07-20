package cn.geekelmon.example.ioc.register;

import cn.geeklemon.core.context.annotation.GeekLemonApplication;
import cn.geeklemon.core.context.annotation.Import;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.context.support.LemonApplication;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2020/1/9 17:11
 * Modified by : kavingu
 */
@GeekLemonApplication
@Import(MyRegister.class)
public class MainC {
    public static void main(String[] args) {
        ApplicationContext context = LemonApplication.run(MainC.class);
        Service service = context.getBean(Service.class);
        service.serve();
    }
}
