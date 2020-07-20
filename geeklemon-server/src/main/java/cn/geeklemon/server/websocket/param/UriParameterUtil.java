package cn.geeklemon.server.websocket.param;

import cn.geeklemon.server.websocket.support.WebSocketEndPointDefine;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/22 10:09
 * Modified by : kavingu
 */
public class UriParameterUtil {
    private static Pattern regex = Pattern.compile("\\{([^}]*)\\}");


    public static WebSocketUrlParam parameters(String uri, String matchUri, Channel channel) {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, CharsetUtil.UTF_8);
        Map<String, List<String>> paramMap = queryDecoder.parameters();

        final WebSocketUrlParam map = new WebSocketUrlParam(channel);

        paramMap.forEach((str, list) -> {
            map.put(str, list.get(0));
        });
        map.putAll(parsePathVariable(uri, matchUri));
        return map;
    }

    private static Map<String, String> parsePathVariable(String uri, String uriMatch) {
        Map<String, String> map = new HashMap<>();
        if (StrUtil.isBlank(uri) || StrUtil.isBlank(uriMatch)) {
            return map;
        }
        String[] uriArray = uri.split("\\?")[0].split("/");
        String[] matchArray = uriMatch.split("/");

        if (uriArray.length != matchArray.length) {
            return map;
        }
        for (int i = 0; i < matchArray.length; i++) {
            String path = uriArray[i];
            String match = matchArray[i];
            Matcher matcher = regex.matcher(match);
            if (matcher.find()) {

                String group = matcher.group(0);
                //去除大括号得到参数名
                group = group.replaceAll("\\{", "");
                group = group.replaceAll("}", "");
                map.put(group, path);
            }
        }
        return map;
    }

    public static boolean match(String path, String match) {
        try {
            String[] pathStr = path.split("\\?")[0].split("/");
            String[] matchStr = match.split("/");
            if (pathStr.length != matchStr.length) {
                return false;
            }
            for (int i = 0; i < matchStr.length; i++) {
                String p = pathStr[i];
                String m = matchStr[i];
                if (StrUtil.equals(p, m)) {
                    continue;
                }
                Matcher matcher = regex.matcher(match);
                if (!matcher.find()) {
                    /*不相等并且不匹配路径参数*/
                    return false;
                }
            }
            return true;
        } catch (Exception e) {

            return false;
        }
    }
}
