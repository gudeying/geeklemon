package cn.geekelmon.example.ioc.asm.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/18 9:20
 * Modified by : kavingu
 */
public class AsmTestUser implements Serializable {
    private String name;
    private Date birth;

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
}
