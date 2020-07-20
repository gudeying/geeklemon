package cn.geeklemon.server.common;

import cn.geeklemon.server.TemporaryDataHolder;
import cn.geeklemon.server.context.LemonServerWebContext;
import cn.geeklemon.server.controller.ControllerDefine;
import cn.geeklemon.server.request.HttpRequest;
import cn.geeklemon.server.request.LemonHttpRequest;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/23 9:36 Modified by : kavingu
 */
public class PathUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathUtil.class);

    private static Pattern regex = Pattern.compile("\\{([^}]*)\\}");

    public static boolean addParamIfAccept(String path, String controllerPath) {
        if (controllerPath.equals(path)) {
            return true;
        }
        try {
            path = URLUtil.getPath(path);// 去除参数
        } catch (Exception e) {
            LOGGER.error(path);
            return false;
        }
        if (controllerPath.equals(path)) {
            return true;
        }
        String[] split = path.split("/");
        String[] paths = controllerPath.split("/");
        if (!(split.length == paths.length) && !controllerPath.endsWith("**")) {
            return false;
        }
        for (int i = 0; i < paths.length; i++) {
            String match = paths[i];
            String str = split[i];
            if ("**".equals(match)) {
                return true;
            }
            if (!match(match, str)) {
                return false;
            }
        }
        return true;
    }

    private static boolean match(String match, String str2) {
        if (match.equals("*")) {
            /* 通配符 */
            return true;
        }
        if (match.equals(str2)) {
            /* 相同 */
            return true;
        }
        /**
         * 看是否是路径匹配
         */
        Matcher matcher = regex.matcher(match);
        if (matcher.find()) {
            /*一次双斜线之间只允许一个参数 */
            String group = matcher.group(0);
            // 去除大括号得到参数名
            group = group.replaceAll("\\{", "");
            group = group.replaceAll("}", "");
            HttpRequest httpRequest = TemporaryDataHolder.loadLemonRequest();
            if (httpRequest instanceof LemonHttpRequest) {
                LemonHttpRequest lemonHttpRequest = (LemonHttpRequest) httpRequest;
                lemonHttpRequest.addParam(group, str2);
                TemporaryDataHolder.storeLemonRequest(lemonHttpRequest);
            }
            return true;
        }
        return false;
    }

    public static boolean pathMatch(String requestUrl, String pathToMatch) {

        if (requestUrl.equals(pathToMatch)) {
            return true;
        }
        try {
            requestUrl = URLUtil.getPath(requestUrl);// 去除参数
        } catch (Exception e) {
            // 非法请求
            LOGGER.error(requestUrl);
            return false;
        }
        if (requestUrl.equals(pathToMatch)) {
            return true;
        }

        String[] split = requestUrl.split("/");
        if (ArrayUtil.isEmpty(split)) {
            /* url为 "/" 的时候会出现，通配在之前判断过了 */
            return false;
        }
        String[] paths = pathToMatch.split("/");
        if (!(split.length == paths.length) && !pathToMatch.endsWith("**")) {
            return false;
        }
        for (int i = 0; i < paths.length; i++) {
            String match = paths[i];
            String str = split[i];
            if (i == paths.length - 1) {
                /* 最后一个参数 */
                str = str.split("\\?")[0];
            }
            if ("**".equals(match)) {
                /* 全通配符可以直接return true */
                return true;
            }
            if (match.equals(str)) {
                /* 相同 */
                continue;
            }
            if (!match.equals("*")) {
                /* 通配符 */
                return false;
            }

        }
        return true;

    }
}
