package cn.geekelmon.app.api.entity;

/**
 * 默认更新信息实体
 */
public class UpdateInfo {
    private int Code;
    private String Msg;
    /**
     *  0代表不更新，1代表有版本更新，不需要强制升级，2代表有版本更新，需要强制升级
     */
    private int UpdateStatus;
    private int VersionCode;
    private String VersionName;
    private String ModifyContent;
    private String DownloadUrl;
    private int AppSize;
    private String ApkMd5;

    public UpdateInfo() {
    }

    public UpdateInfo(Builder builder) {
        setCode(builder.Code);
        setMsg(builder.Msg);
        setUpdateStatus(builder.UpdateStatus);
        setVersionCode(builder.VersionCode);
        setVersionName(builder.VersionName);
        setModifyContent(builder.ModifyContent);
        setDownloadUrl(builder.DownloadUrl);
        setAppSize(builder.AppSize);
        setApkMd5(builder.ApkMd5);
    }


    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public int getUpdateStatus() {
        return UpdateStatus;
    }

    public void setUpdateStatus(int updateStatus) {
        UpdateStatus = updateStatus;
    }

    public int getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(int versionCode) {
        VersionCode = versionCode;
    }

    public String getVersionName() {
        return VersionName;
    }

    public void setVersionName(String versionName) {
        VersionName = versionName;
    }

    public String getModifyContent() {
        return ModifyContent;
    }

    public void setModifyContent(String modifyContent) {
        ModifyContent = modifyContent;
    }

    public String getDownloadUrl() {
        return DownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        DownloadUrl = downloadUrl;
    }

    public int getAppSize() {
        return AppSize;
    }

    public void setAppSize(int appSize) {
        AppSize = appSize;
    }

    public String getApkMd5() {
        return ApkMd5;
    }

    public void setApkMd5(String apkMd5) {
        ApkMd5 = apkMd5;
    }

    public static final class Builder {
        private int Code;
        private String Msg;
        private int UpdateStatus;
        private int VersionCode;
        private String VersionName;
        private String ModifyContent;
        private String DownloadUrl;
        private int AppSize;
        private String ApkMd5;

        public Builder() {
        }

        public Builder Code(int val) {
            Code = val;
            return this;
        }

        public Builder Msg(String val) {
            Msg = val;
            return this;
        }

        public Builder UpdateStatus(int val) {
            UpdateStatus = val;
            return this;
        }

        public Builder VersionCode(int val) {
            VersionCode = val;
            return this;
        }

        public Builder VersionName(String val) {
            VersionName = val;
            return this;
        }

        public Builder ModifyContent(String val) {
            ModifyContent = val;
            return this;
        }

        public Builder DownloadUrl(String val) {
            DownloadUrl = val;
            return this;
        }

        public Builder AppSize(int val) {
            AppSize = val;
            return this;
        }

        public Builder ApkMd5(String val) {
            ApkMd5 = val;
            return this;
        }

        public UpdateInfo build() {
            return new UpdateInfo(this);
        }
    }
}
