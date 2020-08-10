package com.zut.lpf.lidou.util;

import com.zut.lpf.lidou.service.MessageService;
import com.zut.lpf.lidou.service.impl.MessageServiceImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

public class text {
    static String s;
    static int index;
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {

        Scanner in = new Scanner(System.in);
        int t=in.nextInt();
        while(t!=0)
        {
            t--;
            int n=in.nextInt();
            int a[]=new int[n];
            for(int i=0;i<n;i++)
                a[i]=in.nextInt();
            Arrays.sort(a);
            int i=0;
            int sum=0;

            while (true)
            {
                if(a[i]!=0)
                    break;
                i++;
                sum++;
            }
            String s="";

            BigInteger f=new BigInteger(a[i]+"");
            for(int j=i+1;j<n;j++)
            {

                if(j==i+1)
                {
                    s+=(char)(a[j]+'0');
                    for(int k=0;k<sum;k++)
                        s+=(char)(0+'0');
                }
                else
                {
                    s+=(char)(a[j]+'0');
                }
            }

            BigInteger e=new BigInteger(s);
            System.out.println(f.multiply(e));

        }
    }
}
