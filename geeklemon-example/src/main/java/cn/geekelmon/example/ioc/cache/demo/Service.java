package cn.geekelmon.example.ioc.cache.demo;

import cn.geekelmon.cache.annotation.LCache;
import cn.geeklemon.core.context.annotation.Bean;

@Bean
public class Service {
    @LCache(log = true)
    public String test() {
        System.out.println("获取中...");
        return "value";
    }
}
