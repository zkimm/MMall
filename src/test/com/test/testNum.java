package com.test;

import org.junit.Test;

import java.math.BigDecimal;

public class testNum {

    @Test
    public void test_01(){
        System.out.println(0.05-0.01);
        System.out.println(1.0-0.42);
        System.out.println(4.015*100);
        System.out.println(123.3/100);
        BigDecimal b1=new BigDecimal(0.05);
        BigDecimal b2=new BigDecimal(0.01);
        System.out.println(b1.add(b2));
        System.out.println(b1.add(b2));
        
    }
}
