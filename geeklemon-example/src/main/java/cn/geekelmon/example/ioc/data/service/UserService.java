package cn.geekelmon.example.ioc.data.service;

import cn.geekelmon.example.ioc.data.entity.User;
import cn.geekelmon.example.ioc.data.mapper.UserMapper;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Bean;

import java.util.List;
import java.util.Map;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2020/1/10 11:11
 * Modified by : kavingu
 */
@Bean
public class UserService {
    @Autowired
    private UserMapper mapper;

    public List<Map> user() {
        return mapper.result();
    }

    public List<User> userList() {
        return mapper.userList();
    }
}
