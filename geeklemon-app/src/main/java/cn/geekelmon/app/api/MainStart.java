package cn.geekelmon.app.api;

import java.io.File;

import cn.geekelmon.app.api.util.ImgToBase64;
import cn.geekelmon.cache.annotation.EnableCache;
import cn.geekelmon.data.annotation.EnableDataConfig;
import cn.geeklemon.core.context.annotation.GeekLemonApplication;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.context.support.LemonApplication;
import cn.geeklemon.core.util.ResourceUtils;
import cn.geeklemon.server.auto.WebApplication;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;

/**
 */
@GeekLemonApplication
@WebApplication
@EnableDataConfig
@EnableCache
public class MainStart {
    public static void main(String[] args) {
        ResourceLeakDetector.setLevel(Level.PARANOID);
        /**
         * 使用 java -Dfile.encoding=utf-8 下载模板渲染的word文件不会报错
         */
        String property = System.getProperty("file.encoding");
        System.out.println(property);

        ResourceUtils.addAcceptFileType("HTML");

        ResourceUtils.addAcceptFileType("WOFF2");

        ResourceUtils.addAcceptFileType("MAP");

        ResourceUtils.addAcceptFileType("WEBP");
        ResourceUtils.addAcceptFileType("TXT");

        ResourceUtils.addResource("/mysite/");
//        ResourceUtils.addResource("G:/videos/");
        ApplicationContext context = LemonApplication.run(MainStart.class);

int a[] = new int[5];
       for (int i=0;i<a.length-1;i++){
           for (int j=0;j<a.length-1;j++){
               if (a[j]>a[j+1]){
                   int temp = a[j];
                   a[j] = a[j+1];
                   a[j+1]= temp;
               }
           }
        }
    }

}
