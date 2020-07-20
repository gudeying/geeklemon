package cn.geekelmon.example.ioc.server.service;

import cn.geekelmon.example.ioc.Annotation.Log;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/18 11:32
 * Modified by : kavingu
 */
public interface UserServiceInterface {
    @Log("getUserByName")
    String getUserByName(String user);
}
