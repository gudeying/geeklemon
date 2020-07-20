package cn.geekelmon.data.tool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author : Kavin Gu
 * Project Name : redant
 * Description :
 * @version : ${VERSION} 2019/3/2 13:43
 * Modified by : kavingu
 */
public class ReflectTool {
    public static Object invokeMethod(Object target, String mName, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (args == null) {
            args = new Object[0];
        }
        Method method = target.getClass().getMethod(mName, getParamTypeArr(args));
        return method.invoke(target, args);
    }

    /**
     * 根据参数获取参数类型数组，方便只用方法名执行反射
     *
     * @param args
     * @return
     */
    public static Class<?>[] getParamTypeArr(Object... args) {
        if (args.length==0) {
            return new Class<?>[0];
        }
        Class<?>[] classes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                classes[i] = Object.class;
            } else {
                classes[i] = args[i].getClass();
            }
        }
        return classes;
    }
}
