package com.mmall.util;

import java.math.BigDecimal;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午1:24 2018/6/22
 * @ Description：对double型的变量实现不丢失精度的加减乘除
 */
public class BigDecimalUtil {
    private BigDecimalUtil(){
    }


    public static BigDecimal add(double a,double b){
        BigDecimal bigDecimal1=new BigDecimal(Double.toString(a));
        BigDecimal bigDecimal2=new BigDecimal(Double.toString(b));
        return bigDecimal1.add(bigDecimal2);
    }

    public static BigDecimal sub(double a,double b){
        BigDecimal bigDecimal1=new BigDecimal(Double.toString(a));
        BigDecimal bigDecimal2=new BigDecimal(Double.toString(b));
        return bigDecimal1.subtract(bigDecimal2);
    }

    public static BigDecimal mul(double a,double b){
        BigDecimal bigDecimal1=new BigDecimal(Double.toString(a));
        BigDecimal bigDecimal2=new BigDecimal(Double.toString(b));
        return bigDecimal1.multiply(bigDecimal2);
    }

    public static BigDecimal div(double a,double b){
        BigDecimal bigDecimal1=new BigDecimal(Double.toString(a));
        BigDecimal bigDecimal2=new BigDecimal(Double.toString(b));
        return bigDecimal1.divide(bigDecimal2,2,BigDecimal.ROUND_HALF_UP);
    }
}
