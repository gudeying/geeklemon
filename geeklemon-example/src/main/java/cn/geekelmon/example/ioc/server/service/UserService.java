package cn.geekelmon.example.ioc.server.service;

import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.core.context.annotation.Import;
import cn.geeklemon.core.context.annotation.Value;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/11 16:42
 * Modified by : kavingu
 */
@Bean
@Import(UserDao.class)
public class UserService implements UserServiceInterface {
    @Value
    private String name;
    @Autowired
    private UserDao userDao;

    @Override
    public String getUserByName(String user) {
        System.out.println("【执行】");
        return user + " name: " + name;
    }

    @Override
    public String toString() {
        return "UserService{" +
                "name='" + name + '\'' +
                '}';
    }
}
