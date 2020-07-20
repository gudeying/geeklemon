package cn.geekelmon.app.api.aop;

import cn.geeklemon.core.aop.AopType;
import cn.geeklemon.core.aop.PointDefine;
import cn.geeklemon.core.aop.annotation.AopPoint;
import cn.geeklemon.core.aop.annotation.AopProxy;
import cn.geeklemon.core.aop.annotation.PointCut;

@AopProxy
public class LogAop {

    @AopPoint(type = AopType.AROUND, value = Log.class)
    public void log(PointDefine define) {
        System.out.println("around log");
    }

    @AopPoint(type = AopType.AFTER, value = Log.class)
    public void atgerLog(PointDefine define) {
        System.out.println("after log");
    }

    @PointCut(value = "cn.geekelmon.app.api.service.VersionService.lastVersion()")
    public void str(PointDefine define) {
        System.out.println("查询版本 around");
    }
}
