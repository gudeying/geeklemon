package cn.geekelmon.example.ioc.server.entity;

import java.util.Date;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/11 15:51
 * Modified by : kavingu
 */
public class TestUser {
    private String name;
    private Date birth;
    private int age;

    public TestUser(String name, Date birth, int age) {
        this.name = name;
        this.birth = birth;
        this.age = age;
    }

    public TestUser() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "TestUser{" +
                "name='" + name + '\'' +
                ", birth=" + birth +
                ", age=" + age +
                '}';
    }
}
