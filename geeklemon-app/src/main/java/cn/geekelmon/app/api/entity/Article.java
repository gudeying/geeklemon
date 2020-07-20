package cn.geekelmon.app.api.entity;

import java.io.Serializable;
import java.util.Date;

public class Article implements Serializable {
    private int id;
    private String title;
    private String logoSrc;
    private String content;

    private int starNum;
    private int collectionNum;

    /**
     * 使用浏览器打开时，可以直接使用这个
     */
    private String browserUrl;
    /**
     * 分类条目
     */
    private String subject;

    /**
     * 作者openId
     */
    private String authorOpenId;

    private String authorName;
    private String authorLogoUrl;
    /**
     * 当前用户是否收藏了此文章
     */
    private boolean stared;
    /**
     * 当前用户是否订阅了此文章的作者
     */
    private boolean followed;

    private Date writeDate;

    public String getAuthorName() {
        return authorName;
    }

    public Article setAuthorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public String getAuthorLogoUrl() {
        return authorLogoUrl;
    }

    public Article setAuthorLogoUrl(String authorLogoUrl) {
        this.authorLogoUrl = authorLogoUrl;
        return this;
    }

    public boolean isStared() {
        return stared;
    }

    public Article setStared(boolean stared) {
        this.stared = stared;
        return this;
    }

    public boolean isFollowed() {
        return followed;
    }

    public Article setFollowed(boolean followed) {
        this.followed = followed;
        return this;
    }

    private Article(Builder builder) {
        setId(builder.id);
        setTitle(builder.title);
        setLogoSrc(builder.logoSrc);
        setContent(builder.content);
        setStarNum(builder.starNum);
        setCollectionNum(builder.collectionNum);
        setBrowserUrl(builder.browserUrl);
        setSubject(builder.subject);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogoSrc() {
        return logoSrc;
    }

    public void setLogoSrc(String logoSrc) {
        this.logoSrc = logoSrc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStarNum() {
        return starNum;
    }

    public void setStarNum(int starNum) {
        this.starNum = starNum;
    }

    public int getCollectionNum() {
        return collectionNum;
    }

    public void setCollectionNum(int collectionNum) {
        this.collectionNum = collectionNum;
    }

    public String getBrowserUrl() {
        return browserUrl;
    }

    public void setBrowserUrl(String browserUrl) {
        this.browserUrl = browserUrl;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAuthorOpenId() {
        return authorOpenId;
    }

    public Article setAuthorOpenId(String authorOpenId) {
        this.authorOpenId = authorOpenId;
        return this;
    }

    public Date getWriteDate() {
        return writeDate;
    }

    public Article setWriteDate(Date writeDate) {
        this.writeDate = writeDate;
        return this;
    }


    public static final class Builder {
        private int id;
        private String title;
        private String logoSrc;
        private String content;
        private int starNum;
        private int collectionNum;
        private String browserUrl;
        private String subject;

        public Builder() {
        }

        public Builder id(int val) {
            id = val;
            return this;
        }

        public Builder title(String val) {
            title = val;
            return this;
        }

        public Builder logoSrc(String val) {
            logoSrc = val;
            return this;
        }

        public Builder content(String val) {
            content = val;
            return this;
        }

        public Builder starNum(int val) {
            starNum = val;
            return this;
        }

        public Builder collectionNum(int val) {
            collectionNum = val;
            return this;
        }

        public Builder browserUrl(String val) {
            browserUrl = val;
            return this;
        }

        public Builder subject(String val) {
            subject = val;
            return this;
        }

        public Article build() {
            return new Article(this);
        }
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
