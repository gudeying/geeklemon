package cn.geekelmon.example.ioc.server.aop;

import cn.geekelmon.example.ioc.Annotation.Log;
import cn.geekelmon.example.ioc.server.service.UserDao;
import cn.geeklemon.core.aop.AopType;
import cn.geeklemon.core.aop.PointDefine;
import cn.geeklemon.core.aop.annotation.AopPoint;
import cn.geeklemon.core.aop.annotation.AopProxy;
import cn.geeklemon.core.context.annotation.Autowired;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/19 10:05
 * Modified by : kavingu
 */
@AopProxy
public class AopService {
    @Autowired
    private UserDao userDao;

    @AopPoint(type = AopType.BEFORE, value = Log.class)
    public void log(PointDefine define) {
        System.out.println("---------【before】------------");
//        Annotation annotation = define.getAnnotation();
//        if (annotation instanceof Log) {
//            String value = ((Log) annotation).value();
//            userDao.saveLog("log before切面日志：" + value);
//        }

    }

    @AopPoint(type = AopType.AFTER, value = Log.class)
    public void logAfter(PointDefine define) {
        System.out.println("---------【after】-------------");
//        System.out.println(define.getAnnotation());
    }

    @Override
    public String toString() {
        return "AopService{" +
                "userDao=" + userDao +
                '}';
    }
}
