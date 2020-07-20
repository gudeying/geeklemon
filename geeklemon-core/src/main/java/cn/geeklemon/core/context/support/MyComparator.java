package cn.geeklemon.core.context.support;

import cn.geeklemon.core.order.Order;

import java.util.Comparator;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/20 9:08
 * Modified by : kavingu
 */
public class MyComparator implements Comparator<Object> {

    @Override
    public int compare(Object o1, Object o2) {
        int o1Order = 0;
        int o2Order = 0;
        if (o1 instanceof Order) {
            o1Order = ((Order) o1).getOrder();
        }
        if (o2 instanceof Order) {
            o2Order = ((Order) o2).getOrder();
        }
        return o1Order - o2Order;
    }
}
