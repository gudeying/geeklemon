package cn.geekelmon.example.ioc.aopexam;

public class Point2 implements Chain.Point {

    @Override
    public Object proceed(Chain chain) {
        System.out.println("point 2 before");

//        Object result = chain.proceed();
        String result = "point2直接返回结果";
        System.out.println("point 2 after");
        return result;
    }
}