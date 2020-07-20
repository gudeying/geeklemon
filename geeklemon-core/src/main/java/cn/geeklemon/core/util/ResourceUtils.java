package cn.geeklemon.core.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 */
public class ResourceUtils {
    private static Set<String> resourcePaths = new HashSet<>();
    private static final Set<String> acceptFileRequestType;

    private static final Map<String, String> fileContentType = new HashMap<String, String>();

    private static final String defaultFileType = "application/octet-stream";

    static {
        resourcePaths.add("static/");
    }

    static {
        acceptFileRequestType = new HashSet<>();
        acceptFileRequestType.add("PNG");
        acceptFileRequestType.add("JPG");
        acceptFileRequestType.add("GIF");

        acceptFileRequestType.add("JS");
        acceptFileRequestType.add("CSS");
        acceptFileRequestType.add("ICO");

        acceptFileRequestType.add("PDF");
        acceptFileRequestType.add("MP4");
        acceptFileRequestType.add("MP3");
        acceptFileRequestType.add("APK");
    }

    static {
        fileContentType.put("AVI", "video/x-msvideo");
        fileContentType.put("BAS", "text/plain");
        fileContentType.put("BMP", "image/bmp");
        fileContentType.put("CSS", "text/css");
        fileContentType.put("DOC", "application/msword");
        fileContentType.put("DOT", "application/msword");
        fileContentType.put("GIF", "image/gif");
        fileContentType.put("GZ", "application/x-gzip");
        fileContentType.put("HTM", "text/html");
        fileContentType.put("HTML", "text/html");
        fileContentType.put("ICO", "image/x-icon");
        fileContentType.put("JPE", "image/jpeg");
        fileContentType.put("JPEG", "image/jpeg");
        fileContentType.put("JPG", "image/jpeg");
        fileContentType.put("JS", "application/x-javascript");
        fileContentType.put("MHT", "message/rfc822");
        fileContentType.put("MHTML", "message/rfc822");
        fileContentType.put("MOV", "video/quicktime");
        fileContentType.put("MOVIE", "video/x-sgi-movie");
        fileContentType.put("MP2", "video/mpeg");
        fileContentType.put("MP3", "audio/mpeg");
        fileContentType.put("MP4", "video/mpeg4");
        fileContentType.put("MPA", "video/mpeg");
        fileContentType.put("MPE", "video/mpeg");
        fileContentType.put("MPEG", "video/mpeg");
        fileContentType.put("MPG", "video/mpeg");
        fileContentType.put("MPV2", "video/mpeg");
        fileContentType.put("PDF", "application/pdf");
        fileContentType.put("POT", "application/vnd.ms-powerpoint");
        fileContentType.put("PPM", "image/x-portable-pixfileContentType");
        fileContentType.put("PPS", "application/vnd.ms-powerpoint");
        fileContentType.put("PPT", "application/vnd.ms-powerpoint");
        fileContentType.put("PRF", "application/pics-rules");
        fileContentType.put("ROFF", "application/x-troff");
        fileContentType.put("RTF", "application/rtf");
        fileContentType.put("SVG", "image/svg+xml");
        fileContentType.put("TGZ", "application/x-compressed");
        fileContentType.put("TIF", "image/tiff");
        fileContentType.put("TIFF", "image/tiff");
        fileContentType.put("TR", "application/x-troff");
        fileContentType.put("TXT", "text/plain");
        fileContentType.put("WPS", "application/vnd.ms-works");
        fileContentType.put("XLA", "application/vnd.ms-excel");
        fileContentType.put("XLC", "application/vnd.ms-excel");
        fileContentType.put("XLM", "application/vnd.ms-excel");
        fileContentType.put("XLS", "application/vnd.ms-excel");
        fileContentType.put("XLT", "application/vnd.ms-excel");
        fileContentType.put("XLW", "application/vnd.ms-excel");
        fileContentType.put("ZIP", "application/zip");
        fileContentType.put("WOFF2", "font/woff2");

    }

    private ResourceUtils() {

    }

    /**
     * 在配置的外部路径找文件 在classpath下的文件也可以找到(非jar包模式)
     *
     * @param pathAndName 文件路径，省略配置的前prefix
     * @return 目标文件或者null
     */
    public static File getFile(String pathAndName) {
        pathAndName = StrUtil.removePrefix(pathAndName, "/");
        for (String resourcePath : resourcePaths) {
            File file = FileUtil.file(resourcePath + pathAndName);
            if (FileUtil.isFile(file)) {
                return file;
            }
        }
        return null;
    }

    /**
     * 添加额外的静态文件目录<br/>
     * 系统默认classpath:static下为静态资源目录
     *
     * @param path 目录
     */
    public static void addResource(String path) {
        if (StrUtil.isNotBlank(path)) {
            // 添加 "/"结尾
            path = StrUtil.addSuffixIfNot(path, "/");
            resourcePaths.add(path);
        }
    }

    /**
     * 添加允许自动检测的文件类型，文件处理将自动找到文件进行传输(如果resource)
     *
     * @param fileSuffix 文件后缀名，不含点。如 jpg，png
     */
    public static void addAcceptFileType(String... fileSuffix) {
        if (ArrayUtil.isNotEmpty(fileSuffix)) {
            for (String suffix : fileSuffix) {
                if (StrUtil.isNotBlank(suffix)) {
                    acceptFileRequestType.add(suffix.toUpperCase());
                }
            }
        }
    }

    /**
     * 禁止传输的文件类型
     *
     * @param fileSuffix 文件后缀名，不含点。如 jpg，png
     */
    public static void disableFileType(String fileSuffix) {
        if (StrUtil.isNotBlank(fileSuffix)) {
            acceptFileRequestType.remove(fileSuffix.toUpperCase());
        }
    }

    public static boolean fileAccept(String suffix) {
        if (StrUtil.isBlank(suffix)) {
            return false;
        }
        String upperCase = suffix.toUpperCase();
        return acceptFileRequestType.contains(upperCase);
    }

    /**
     * @param shufix 文件后缀名
     * @return 非空 type
     */
    public static String getFIleContentType(String shufix) {
        shufix = shufix.toUpperCase();
        String string = fileContentType.get(shufix);
        if (string != null) {
            return string;
        }
        return defaultFileType;
    }

}
