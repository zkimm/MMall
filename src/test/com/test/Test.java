package com.test;

public class Test {

    private static int id=1;

    static {
        System.out.println("静态代码块");
        show();
    }

    {
        System.out.println("普通代码块");
    }

    public Test(){
        System.out.println("无参构造函数");
    }

    public Test(int id){
        this.id=id;
    }

    public static void setId(int id) {
        Test.id = id;
    }

    public static void show(){
        System.out.println("普通方法^^^"+id);
    }
}
