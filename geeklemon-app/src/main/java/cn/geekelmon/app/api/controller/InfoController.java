package cn.geekelmon.app.api.controller;

import cn.geekelmon.app.api.entity.UpdateInfo;
import cn.geekelmon.app.api.entity.VersionInfo;
import cn.geekelmon.app.api.service.VersionService;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.request.HttpRequest;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;

import java.util.List;
import java.util.Map;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/11/12 17:04
 * Modified by : kavingu
 */
@Controller
public class InfoController {
    @Autowired
    private VersionService versionService;

    @Mapping(path = "/app/info/update/check")
    public UpdateInfo updateInfo(HttpRequest request) {
        String versionCodeString = request.getParameter("versionCode");//后台会传数字versionCode
        int versionCode = NumberUtil.parseInt(versionCodeString);
        VersionInfo versionInfo = versionService.lastVersion();
        if (ObjectUtil.isNotNull(versionInfo) && versionInfo.getVersionCode() > versionCode) {
            //有新版本
            UpdateInfo updateInfo = new UpdateInfo.Builder()
                    .Code(0)
                    .UpdateStatus(1)
                    .VersionCode(versionInfo.getVersionCode())
                    .VersionName(versionInfo.getVersionName())
                    .ModifyContent(versionInfo.getContent())
                    .DownloadUrl(versionInfo.getLoadUrl())
                    .AppSize(versionInfo.getSize())
                    .ApkMd5(versionInfo.getMd5())
                    .build();
            return updateInfo;
        }
        UpdateInfo info = new UpdateInfo.Builder()
                .Code(0)//没有错误
                .UpdateStatus(0)
                .ModifyContent("最新的哦")
                .VersionCode(0)
                .build();
        return info;
    }
}
