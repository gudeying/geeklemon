package cn.geekelmon.example.ioc.aop.test;

import cn.geekelmon.example.ioc.aop.Log;
import cn.geeklemon.core.context.annotation.Bean;

@Bean
public class Service {

    public void test1(String name) {
        System.out.println("【执行】" + name);
    }

    @Log
    public void test2(String name) {
        System.out.println("【执行】" + name);
    }
}
