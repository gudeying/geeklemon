package cn.geekelmon.app.api.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * Modified by : kavingu
 */
public class HtmlUtil {

    /**
     * 将src为相对路径的html字符串中的相对路径替换为绝对路径
     *
     * @param htmlContent html字符串
     * @param baseUrl     服务器地址，必须是http://开头
     * @return 转换后的字符串
     * <p>
     * audio 再WebView中内存泄漏未解决，暂时只给图片进行替换
     */
    public static String processSrc(String htmlContent, String baseUrl) {
        String content = "";
        if (StrUtil.hasBlank(htmlContent, baseUrl)) {
            return content;
        }

        try {
            Document document = Jsoup.parse(htmlContent);
            document.setBaseUri(baseUrl);
            //document.select("audio").remove();
            //document.select("video").remove();
            Elements elements = document.select("[src]");//elements不会为空
            for (Element el : elements) {

                String img = el.attr("src");
                if (!img.trim().startsWith("http")) {
                    //解决图片宽度太大，主要是ueditor手动上传的图片
                    StringBuilder style = new StringBuilder("max-width:90%;");
                    String width = el.attr("width");
                    String height = el.attr("height");
                    if (NumberUtil.isNumber(width)) {
                        el.removeAttr("width");
                        style.append("width:").append(width).append(";");
                    }
                    if (NumberUtil.isNumber(height)) {
                        el.removeAttr("height");
                        style.append("height:").append(height).append(";");
                    }
                    el.attr("style", style.toString());

                    //转为绝对路径
                    el.attr("src", el.absUrl("src"));
                }
            }
            /**
             * 对图片添加点击预览事件
             */
            Elements imgSelect = document.select("img");
            for (Element element : imgSelect) {
                element.attr("onclick",
                        "window.imageLoader.showImg(this.src)");
            }
            content = document.html();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
}
