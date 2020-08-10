package com.zut.lpf.lidou.util;

import java.math.BigInteger;

public class wuwu {
    public static void main(String[] args) {
        BigInteger a=new BigInteger("0");
        System.out.println(a);
        pp(a);
        System.out.println(a);

    }
    public static void pp(BigInteger a)
    {
        a.add(new BigInteger("2"));
    }
}
