package cn.geekelmon.app.api.entity;

import cn.geekelmon.data.annotation.LColumn;
import cn.geekelmon.data.annotation.LId;
import cn.geekelmon.data.annotation.LTable;

import java.io.Serializable;

@LTable("user")
public class UserInfo implements Serializable {
    @LId
    @LColumn("openid")
    private String openId;
    @LColumn("name")
    private String name;
    @LColumn("age")
    private Integer age;
    @LColumn("pasw")
    private String password;
    @LColumn("sex")
    private String sex;
    @LColumn("userlogo")
    private String userLogo;
    @LColumn("mail")
    private String mail;
    @LColumn("level")
    private Integer level;
    @LColumn("autograph")
    private String signature;
    @LColumn("nickName")
    private String nickName;
    private boolean followed;

    public UserInfo() {
    }

    private UserInfo(Builder builder) {
        setOpenId(builder.openId);
        setName(builder.name);
        setAge(builder.age);
        setPassword(builder.password);
        setSex(builder.sex);
        setUserLogo(builder.userLogo);
        setMail(builder.mail);
        setLevel(builder.level);
        setSignature(builder.signature);
        setNickName(builder.nickName);
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public void setUserLogo(String userLogo) {
        this.userLogo = userLogo;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isFollowed() {
        return followed;
    }

    public UserInfo setFollowed(boolean followed) {
        this.followed = followed;
        return this;
    }


    public static final class Builder {
        private String openId;
        private String name;
        private int age;
        private String password;
        private String sex;
        private String userLogo;
        private String mail;
        private int level;
        private String signature;
        private String nickName;

        public Builder() {
        }

        public Builder openId(String val) {
            openId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder age(int val) {
            age = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder sex(String val) {
            sex = val;
            return this;
        }

        public Builder userLogo(String val) {
            userLogo = val;
            return this;
        }

        public Builder mail(String val) {
            mail = val;
            return this;
        }

        public Builder level(int val) {
            level = val;
            return this;
        }

        public Builder signature(String val) {
            signature = val;
            return this;
        }

        public Builder nickName(String val) {
            nickName = val;
            return this;
        }

        public UserInfo build() {
            return new UserInfo(this);
        }
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "openId='" + openId + '\'' +
                ", name='" + name + '\'' +
                ", level=" + level +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInfo userInfo = (UserInfo) o;

        if (openId != null ? !openId.equals(userInfo.openId) : userInfo.openId != null) return false;
        if (name != null ? !name.equals(userInfo.name) : userInfo.name != null) return false;
        if (age != null ? !age.equals(userInfo.age) : userInfo.age != null) return false;
        if (sex != null ? !sex.equals(userInfo.sex) : userInfo.sex != null) return false;
        if (userLogo != null ? !userLogo.equals(userInfo.userLogo) : userInfo.userLogo != null) return false;
        if (mail != null ? !mail.equals(userInfo.mail) : userInfo.mail != null) return false;
        if (level != null ? !level.equals(userInfo.level) : userInfo.level != null) return false;
        if (signature != null ? !signature.equals(userInfo.signature) : userInfo.signature != null) return false;
        return nickName != null ? nickName.equals(userInfo.nickName) : userInfo.nickName == null;
    }

    @Override
    public int hashCode() {
        int result = openId != null ? openId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (userLogo != null ? userLogo.hashCode() : 0);
        result = 31 * result + (mail != null ? mail.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        result = 31 * result + (nickName != null ? nickName.hashCode() : 0);
        return result;
    }
}
