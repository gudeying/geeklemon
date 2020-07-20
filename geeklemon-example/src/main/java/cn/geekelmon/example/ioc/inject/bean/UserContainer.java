package cn.geekelmon.example.ioc.inject.bean;

import cn.geekelmon.example.ioc.data.entity.User;
import cn.geeklemon.core.bean.factory.InitializingBean;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Bean;

@Bean
public class UserContainer implements InitializingBean {
	@Autowired(name = "kavin")
	private UserInfo kavin;
	@Autowired(name = "Lily")
	private UserInfo liLy;

	@Override
	public void afterPropsSet() {
		System.out.println("kavin:" + kavin.getName());
		System.out.println("lily:" + liLy.getName());
	}

}
