package cn.geeklemon.core.exception;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/16 12:58
 * Modified by : kavingu
 */
public class BeanDuplicationException extends Exception {
    public BeanDuplicationException() {
        super("名称重复");
    }

    public BeanDuplicationException(String name) {
        super("名称重复：" + name);
    }
}
