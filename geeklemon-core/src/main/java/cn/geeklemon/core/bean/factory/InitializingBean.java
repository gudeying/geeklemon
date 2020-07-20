package cn.geeklemon.core.bean.factory;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/12 8:44
 * Modified by : kavingu
 */
public interface InitializingBean {
	/**
	 * 不要让线程在这里停下
	 */
    void afterPropsSet();
}
