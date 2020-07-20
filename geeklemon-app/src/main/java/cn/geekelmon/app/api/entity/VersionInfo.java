package cn.geekelmon.app.api.entity;


public class VersionInfo {
    private int id;
    private int versionCode;
    private String versionName;
    private String content;
    private boolean ignorable;
    private String loadUrl;
    private String md5;
    private int size;

    public int getId() {
        return id;
    }

    public VersionInfo setId(int id) {
        this.id = id;
        return this;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public VersionInfo setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    public String getVersionName() {
        return versionName;
    }

    public VersionInfo setVersionName(String versionName) {
        this.versionName = versionName;
        return this;
    }

    public String getContent() {
        return content;
    }

    public VersionInfo setContent(String content) {
        this.content = content;
        return this;
    }

    public boolean isIgnorable() {
        return ignorable;
    }

    public VersionInfo setIgnorable(boolean ignorable) {
        this.ignorable = ignorable;
        return this;
    }

    public String getLoadUrl() {
        return loadUrl;
    }

    public VersionInfo setLoadUrl(String loadUrl) {
        this.loadUrl = loadUrl;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public VersionInfo setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public int getSize() {
        return size;
    }

    public VersionInfo setSize(int size) {
        this.size = size;
        return this;
    }
}
