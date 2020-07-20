package cn.geekelmon.example.ioc.data;

import cn.geekelmon.data.context.DataContext;
import cn.geekelmon.data.support.TransactionPostProcessor;
import cn.geekelmon.example.ioc.data.entity.User;
import cn.geekelmon.example.ioc.data.mapper.UserMapper;
import cn.geekelmon.example.ioc.data.service.TransactionTestService;
import cn.geekelmon.example.ioc.data.service.UserService;
import cn.geeklemon.core.context.annotation.GeekLemonApplication;
import cn.geeklemon.core.context.annotation.Import;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.context.support.LemonApplication;

import java.util.List;
import java.util.Map;

/**
 */
@GeekLemonApplication
@Import({ TransactionPostProcessor.class, DataContext.class })
public class DataMain {
	public static void main2(String[] args) {
		ApplicationContext context = LemonApplication.run(DataMain.class);
		UserMapper service = context.getBean(UserMapper.class);

		UserService userService = context.getBean(UserService.class);
		// userTest(service);
		// mapTest(service);
		// entityTest(service);

		// User user = new User();
		// user.setName("testDynamicInsert");
		// service.insertUser(user);

		// service.update("geek-lemon", 10);

		listUser(service.userList());
		// List<User> userList = service.getUserBySexAndMail("男",
		// "2235733868@qq.com");
		// listUser(userList);

	}

	public static void main(String[] args) {
		ApplicationContext context = LemonApplication.run(DataMain.class);
		TransactionTestService service = context.getBean(TransactionTestService.class);
		int result = service.save(1, true);
		System.out.println(result);
	}

	private static void mapTest(UserService s) {
		List<Map> user = s.user();
		for (Map map : user) {
			map.forEach((kek, val) -> {
				System.out.println(kek + ":" + val);
			});
		}
	}

	private static void entityTest(UserService service) {
		List<User> userList = service.userList();
		for (User user : userList) {
			System.out.println(user);
		}
	}

	private static void userTest(UserMapper mapper) {
		User gudeying = mapper.getUserByName("gudeying");
		System.out.println(gudeying);
	}

	private static void listUser(List<User> users) {
		for (User user : users) {
			System.out.println(user);
		}
	}
}
