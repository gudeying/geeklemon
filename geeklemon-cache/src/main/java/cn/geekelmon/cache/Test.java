package cn.geekelmon.cache;

import java.util.Arrays;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2020/1/17 16:20
 * Modified by : kavingu
 */
public class Test {
    public static void main(String[] args) {
        Object[] objects = new Object[]{"abc", new Integer(124), new Boolean(false)};
        Object[] objects2 = new Object[]{"abc", 124, false};
        System.out.println(Arrays.equals(objects, objects2));
    }
}
