package cn.geeklemon.core.context.support;

import cn.geeklemon.core.bean.factory.BeanScannerFilter;
import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.core.util.AnnotationUtils;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjectUtil;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/12 15:49
 * Modified by : kavingu
 */
public class DefaultBeanScnnerFilter implements BeanScannerFilter {
    @Override
    public Class<?> getBeanType(Class<?> cls) {
        return cls;
    }

    @Override
    public boolean accept(Class<?> o) {
        Bean annotation = AnnotationUtil.getAnnotation(o, Bean.class);
        return annotation != null;
    }

    @Override
    public boolean single(Class<?> cls) {
        Bean annotation = AnnotationUtil.getAnnotation(cls, Bean.class);
        return annotation.single();
    }
}
