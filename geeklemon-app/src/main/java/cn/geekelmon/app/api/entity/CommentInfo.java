package cn.geekelmon.app.api.entity;

import java.io.Serializable;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/11/12 16:25
 * Modified by : kavingu
 */
public class CommentInfo implements Serializable {
    private int id;
    private String userId;
    private String userName;
    private String timeInfo;
    private String content;
    private String userLogo;
    private int parentId;
    private String toWho;

    private CommentInfo(Builder builder) {
        id = builder.id;
        userId = builder.userId;
        userName = builder.userName;
        timeInfo = builder.timeInfo;
        content = builder.content;
        userLogo = builder.userLogo;
        parentId = builder.parentId;
        toWho = builder.toWho;
    }


    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getTimeInfo() {
        return timeInfo;
    }

    public String getContent() {
        return content;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public int getParentId() {
        return parentId;
    }

    public String getToWho() {
        return toWho;
    }

    public static final class Builder {
        private int id;
        private String userId;
        private String userName;
        private String timeInfo;
        private String content;
        private String userLogo;
        private int parentId;
        private String toWho;

        public Builder() {
        }

        public Builder id(int val) {
            id = val;
            return this;
        }

        public Builder userId(String val) {
            userId = val;
            return this;
        }

        public Builder userName(String val) {
            userName = val;
            return this;
        }

        public Builder timeInfo(String val) {
            timeInfo = val;
            return this;
        }

        public Builder content(String val) {
            content = val;
            return this;
        }

        public Builder userLogo(String val) {
            userLogo = val;
            return this;
        }

        public Builder parentId(int val) {
            parentId = val;
            return this;
        }

        public Builder toWho(String val) {
            toWho = val;
            return this;
        }

        public CommentInfo build() {
            return new CommentInfo(this);
        }


    }
}
