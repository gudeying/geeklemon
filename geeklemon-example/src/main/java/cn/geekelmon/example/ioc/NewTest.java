package cn.geekelmon.example.ioc;

import cn.geekelmon.data.annotation.EnableDataConfig;
import cn.geeklemon.server.auto.WebApplication;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/12 16:12
 * Modified by : kavingu
 */
@EnableDataConfig
@WebApplication
public class NewTest {
    public static void main(String[] args) {
//        Annotation[] annotations = NewTest.class.getAnnotations();
//        for (Annotation annotation : annotations) {
//            Class<? extends Annotation> aClass = annotation.annotationType();
//            Import anImport = AnnotationUtil.getAnnotation(aClass, Import.class);
//            if (anImport != null) {
//                Class[] value = anImport.value();
//            }
//        }
//
//        Map<String, Object> annotationValueMap = AnnotationUtil.getAnnotationValueMap(NewTest.class, Import.class);
//        annotationValueMap.forEach((kev, val) -> {
//            Class[] val1 = (Class[]) val;
//            for (Class aClass : val1) {
//                System.out.println(aClass);
//            }
//            System.out.println("-------");
//        });

        System.out.println(boolean.class.getTypeName());
    }
}
