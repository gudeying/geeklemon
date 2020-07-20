package cn.geekelmon.example.ioc.inject.bean;

import cn.geeklemon.core.context.annotation.Bean;

@Bean(name = "Lily", single = false)
public class UserLily implements UserInfo {
	public UserLily() {
		System.out.println("初始化");
	}

	@Override
	public String getName() {
		return "Lily";
	}

	@Override
	public String toString() {
		return getName();
	}

}
