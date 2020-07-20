package cn.geekelmon.example.ioc.register;

import cn.geeklemon.core.context.annotation.Bean;

import java.io.Serializable;

/**
 */
@Bean
public class NeededService implements Serializable {

    public void serve() {
        System.out.println("this is service factory bean needed");
    }
}
