package cn.geekelmon.example.ioc.data.mapper;

import cn.geekelmon.data.annotation.LMapper;
import cn.geekelmon.data.annotation.LParam;
import cn.geekelmon.data.annotation.LemonQuery;
import cn.geekelmon.data.sql.DefaultSqlProvider;
import cn.geekelmon.data.sql.QueryType;
import cn.geekelmon.example.ioc.data.entity.User;

import java.util.List;
import java.util.Map;

/**
 */
@LMapper("user")
public interface UserMapper {
    @LemonQuery(value = "select id,name from user limit 0,15", queryType = QueryType.SELECT)
    List<Map> result();

    @LemonQuery(value = "select * from user")
    List<User> userList();

    @LemonQuery(value = "select * from user where name = ${name}")
    User getUserByName(String name);

    /**
     * 如果参数的顺序和占位符顺序一致，就不需要@Lparam来确定位置
     */
    @LemonQuery(value = "select * from user where mail =${mail} and sex = ${sex}")
    List<User> getUserBySexAndMail(@LParam("sex") String sex, @LParam("mail") String mail);

    @LemonQuery(value = "insert into user (name) values(${name})", queryType = QueryType.INSERT)
    int insert(User user);

    @LemonQuery(queryType = QueryType.INSERT, sqlProviderClass = DefaultSqlProvider.class, sqlProviderMethod = "insert")
    int insertUser(User user);

    @LemonQuery(value = "update user set name = ${name} where id = ${id}", queryType = QueryType.UPDATE)
    void update(String name, int id);
}