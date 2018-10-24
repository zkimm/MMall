package com.util;

import java.math.BigDecimal;

public class BigDecimalUtil {

    public BigDecimalUtil() {
    }

    public static BigDecimal add(double b1,double b2){
        BigDecimal v1=new BigDecimal(Double.toString(b1));
        BigDecimal v2=new BigDecimal(Double.toString(b2));
        return v1.add(v2);
    }

    public static BigDecimal sub(double b1, double b2) {
        BigDecimal v1 = new BigDecimal(Double.toString(b1));
        BigDecimal v2 = new BigDecimal(Double.toString(b2));
        return v1.subtract(v2);
    }

    /**
     * 乘法
     * @param b1
     * @param b2
     * @return
     */
    public static BigDecimal mul(double b1, double b2) {
        BigDecimal v1 = new BigDecimal(Double.toString(b1));
        BigDecimal v2 = new BigDecimal(Double.toString(b2));
        return v1.multiply(v2);
    }

    /**
     * 除法
     * @param b1
     * @param b2
     * @return
     */
    public static BigDecimal div(double b1, double b2) {
        BigDecimal v1 = new BigDecimal(Double.toString(b1));
        BigDecimal v2 = new BigDecimal(Double.toString(b2));
        return v1.divide(v2,2,BigDecimal.ROUND_HALF_UP);
        //使用四舍五入,保留两位小数
    }

}
