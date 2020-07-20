package cn.geekelmon.example.ioc.inject.bean;

import cn.geeklemon.core.context.annotation.Bean;

@Bean(name = "kavin")
public class UserKavin implements UserInfo {

	@Override
	public String getName() {

		return "kavin";
	}

}
