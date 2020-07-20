package cn.geekelmon.example.ioc.server.service;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/12 13:15
 * Modified by : kavingu
 */

public class UserDao {

    public void userDao() {
        System.out.println("user Dao");
    }

    public void saveLog(String log) {
        System.out.println(log);
    }
}
