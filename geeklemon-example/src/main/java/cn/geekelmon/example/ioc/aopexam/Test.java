package cn.geekelmon.example.ioc.aopexam;

public class Test {

    public static void main(String[] args) {
        Object proxy = ProxyFactory.create().getProxy(new SayHello());
        proxy.toString();
    }


    static class SayHello {

        @Override
        public String toString() {
            System.out.println("hello cglib !");
            return "hello cglib !";
        }
    }
}