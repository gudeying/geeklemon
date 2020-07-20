package cn.geekelmon.example.ioc.data.entity;

import cn.geekelmon.data.annotation.LColumn;
import cn.geekelmon.data.annotation.LId;
import cn.geekelmon.data.annotation.LTable;

/**
 */
@LTable("user")
public class User {
    @LId
    @LColumn("id")
    private String id;
    @LColumn("name")
    private String name;

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }
}
