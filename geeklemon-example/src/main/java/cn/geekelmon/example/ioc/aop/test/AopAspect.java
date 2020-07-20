package cn.geekelmon.example.ioc.aop.test;

import cn.geekelmon.example.ioc.aop.Log;
import cn.geeklemon.core.aop.PointDefine;
import cn.geeklemon.core.aop.annotation.AopPoint;
import cn.geeklemon.core.aop.annotation.AopProxy;
import cn.geeklemon.core.aop.annotation.PointCut;

@AopProxy
public class AopAspect {
    @AopPoint(value = Log.class)
    public void test1(PointDefine define) {
        System.out.println("log around");
    }

    @PointCut("cn.geekelmon.example.ioc.aop.test.Service.test1()")
    public void test2(PointDefine define) {
        System.out.println("【around】cn.geekelmon.example.ioc.aop.test.Service.test1()");
    }
}
