package cn.geeklemon.server.request;

import cn.geeklemon.core.util.PrimitiveTypeUtil;
import cn.geeklemon.server.common.ContentType;
import cn.geeklemon.server.multipart.FormDataAttributeRead;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/23 13:13 Modified by : kavingu
 */
public class HttpRequestUtil {
    /**
     * 获取请求参数的Map
     *
     * @param request http请求
     * @return 参数map, 不为null
     */
    public static Map<String, List<String>> getParameterMap(HttpRequest request) {
        Assert.notNull(request);
        Map<String, List<String>> paramMap = new LinkedHashMap<String, List<String>>();

        try {
            HttpMethod method = request.method();
            if (HttpMethod.GET.equals(method)) {
                String uri = request.uri();
                HtmlUtil.filter(uri);
                QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, CharsetUtil.UTF_8);
                paramMap = queryDecoder.parameters();

            } else if (HttpMethod.POST.equals(method)) {
                FullHttpRequest fullRequest = (FullHttpRequest) request;
                paramMap = getPostParamMap(fullRequest);
            }
        } catch (Exception ignored) {

        }

        return paramMap;
    }

    /**
     * 获取post请求的参数map 目前支持最常用的 application/json
     * 、application/x-www-form-urlencoded 几种 POST Content-type，可自行扩展！！！
     */
    @SuppressWarnings("unchecked")
    public static Map<String, List<String>> getPostParamMap(FullHttpRequest fullRequest) {
        Map<String, List<String>> paramMap = new HashMap<>();
        HttpHeaders headers = fullRequest.headers();
        String contentType = getContentType(headers);
        if (contentType == null) {
            QueryStringDecoder queryDecoder = new QueryStringDecoder(fullRequest.uri(), CharsetUtil.UTF_8);
            return queryDecoder.parameters();
        }
        if (ContentType.APPLICATION_JSON.toString().equals(contentType)) {
            String jsonStr = fullRequest.content().toString(CharsetUtil.UTF_8);
            JSONObject obj = JSON.parseObject(jsonStr);
            for (Map.Entry<String, Object> item : obj.entrySet()) {
                String key = item.getKey();
                Object value = item.getValue();
                Class<?> valueType = value.getClass();

                List<String> valueList;
                if (paramMap.containsKey(key)) {
                    valueList = paramMap.get(key);
                } else {
                    valueList = new ArrayList<String>();
                }

                if (PrimitiveTypeUtil.isPriType(valueType)) {
                    valueList.add(value.toString());
                    paramMap.put(key, valueList);

                } else if (valueType.isArray()) {
                    int length = Array.getLength(value);
                    for (int i = 0; i < length; i++) {
                        String arrayItem = String.valueOf(Array.get(value, i));
                        valueList.add(arrayItem);
                    }
                    paramMap.put(key, valueList);

                } else if (List.class.isAssignableFrom(valueType)) {
                    if (valueType.equals(JSONArray.class)) {
                        JSONArray jArray = JSONArray.parseArray(value.toString());
                        for (int i = 0; i < jArray.size(); i++) {
                            valueList.add(jArray.getString(i));
                        }
                    } else {
                        valueList = (ArrayList<String>) value;
                    }
                    paramMap.put(key, valueList);

                } else if (Map.class.isAssignableFrom(valueType)) {
                    Map<String, String> tempMap = (Map<String, String>) value;
                    for (Map.Entry<String, String> entry : tempMap.entrySet()) {
                        List<String> tempList = new ArrayList<String>();
                        tempList.add(entry.getValue());
                        paramMap.put(entry.getKey(), tempList);
                    }
                }
            }

        } else if (ContentType.APPLICATION_FORM_URLENCODED.toString().equals(contentType)) {
            String jsonStr = fullRequest.content().toString(CharsetUtil.UTF_8);
            QueryStringDecoder queryDecoder = new QueryStringDecoder(jsonStr, false);
            paramMap = queryDecoder.parameters();
        } else if (contentType.startsWith(cn.hutool.http.ContentType.MULTIPART.toString())) {
            paramMap = FormDataAttributeRead.paramMap(fullRequest);
        }

        return paramMap;
    }

    /**
     * 获取contentType
     *
     * @param headers http请求头
     * @return 内容类型
     */
    public static String getContentType(HttpHeaders headers) {
        String contentType = headers.get(HttpHeaderNames.CONTENT_TYPE);
        if (contentType == null) {
            return null;
        }
        String[] list = contentType.split(";");
        return list[0];
    }
}
