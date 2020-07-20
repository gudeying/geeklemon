package cn.geekelmon.example.ioc.data.service;

import java.sql.SQLException;

import cn.geekelmon.data.annotation.Transaction;
import cn.geekelmon.data.support.JdbcExecutorFactory;
import cn.geekelmon.data.support.LJdbcExecutor;
import cn.geekelmon.example.ioc.data.entity.TestInfo;
import cn.geekelmon.example.ioc.data.entity.User;
import cn.geekelmon.example.ioc.data.mapper.TestInfoMpper;
import cn.geekelmon.example.ioc.data.mapper.UserMapper;
import cn.geeklemon.core.aop.extra.ExceptionAvoid;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Bean;

@Bean
public class TransactionTestService {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private TestInfoMpper testInfoMpper;
	@Autowired
	private JdbcExecutorFactory factory;

	@ExceptionAvoid
	@Transaction
	public int save(Object object, boolean throwEx) {
		User user = new User();
		user.setName("test_User" + object);

		TestInfo testInfo = new TestInfo();
		testInfo.setMsg("test_info" + object);

		int save = testInfoMpper.save(testInfo);
		System.out.println(save);

		if (throwEx) {
			throw new RuntimeException();
		}
		userMapper.insert(user);
		return 1;
	}

}
