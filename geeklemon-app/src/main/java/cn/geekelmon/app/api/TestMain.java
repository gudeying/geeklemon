package cn.geekelmon.app.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

import cn.geekelmon.app.api.entity.UserInfo;
import cn.geeklemon.core.util.BCrypt;
import cn.geeklemon.core.util.PropsUtil;
import cn.geeklemon.server.common.PathUtil;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.LineHandler;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.*;
import cn.hutool.extra.template.Engine;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateConfig.ResourceMode;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.XML;
import com.sun.jndi.toolkit.url.UrlUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.MessageSizeEstimator.Handle;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCounted;

public class TestMain {

    public static void main(String[] args) {
        try {

            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void SecurityTest() {
        String password =
                BCrypt.hashpw("abc", BCrypt.gensalt(10));
        String check = "abc";
/**
 * 不需要单独存储salt。
 */
        boolean match = BCrypt.checkpw(check, password);
        System.out.println(match);

    }
}
