package cn.geekelmon.example.ioc.Annotation;

import cn.geekelmon.example.ioc.server.aop.AopService;
import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.core.util.AnnotationUtils;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjectUtil;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/18 11:35
 * Modified by : kavingu
 */
public class AnnTest {
    public static void main(String[] args) {
        Bean annotation = AnnotationUtil.getAnnotation(AopService.class, Bean.class);
        System.out.println(ObjectUtil.isNull(annotation));
        System.out.println(AnnotationUtils.contain(AopService.class,Bean.class));
    }
}
