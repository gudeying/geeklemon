package cn.geeklemon.core.util;

import cn.geeklemon.core.context.annotation.Bean;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/10 14:05
 * Modified by : kavingu
 */
public class BeanNameUtil {
    public static String getBeanName(Class<?> cls) {
        Assert.notNull(cls);
        String name = "";
        Bean bean = cls.getAnnotation(Bean.class);
        if (ObjectUtil.isNotNull(bean)) {
            String[] names = bean.name();
            if (names.length >= 1) {
                name = names[0];
            }
        }
        String beanName = cls.getName();
        beanName = StrUtil.isBlank(name) ? beanName : name;
        return beanName;

    }
}
