package cn.geekelmon.example.ioc.data;


import cn.geekelmon.data.LSQLTool;
import cn.geekelmon.example.ioc.data.mapper.UserMapper;
import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.Map;

public class TestParam {
    public static void main(String[] args) {
        Method method = ReflectUtil.getMethod(UserMapper.class, "getUserBySexAndMail", String.class, String.class);
        Map<String, Integer> named = LSQLTool.getParamsMapNamed(method);
        named.forEach((key, val) -> {
            System.out.println(key + ":" + val);
        });
    }
}
