package cn.geekelmon.example.ioc.aopexam;

import cn.hutool.core.util.ReflectUtil;

import java.util.List;

public class Chain {
    private List<Point> list;
    private int index = -1;
    private Object target;

    public Chain(List<Point> list, Object target) {
        this.list = list;
        this.target = target;
    }

    public Object proceed() {
        Object result;
        if (++index == list.size()) {
            result = ReflectUtil.invoke(target, "toString");
//            result = (target.toString());
        } else {
            Point point = list.get(index);
            result = point.proceed(this);
        }
        return result;
    }

    interface Point {
        Object proceed(Chain chain);
    }
}