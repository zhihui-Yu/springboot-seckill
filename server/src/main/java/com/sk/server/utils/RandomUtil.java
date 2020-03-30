package com.sk.server.utils;

import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 随机数生成
 *
 * @Author yzh
 * @Date 2020/3/30 12:33
 * @Version 1.0
 */
public class RandomUtil {

    private static final SimpleDateFormat dateFormatOne = new SimpleDateFormat("yyyyMMddHHmmssSS");

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    //时间戳+N位随机数
    public static String generaterOrderCode(){
        return dateFormatOne.format(DateTime.now().toDate())+generateNumber(4);
    }

    public static String generateNumber(final int num) {
        StringBuffer sb = new StringBuffer();

        for (int i = 1; i <= num;i++) {
            sb.append(RANDOM.nextInt(9));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap map = new ConcurrentHashMap();
        for (int i = 0; i < 10000; i++) {
            new Thread(()->{
                for (int j = 0; j < 1000; j++) {
                    String num = generaterOrderCode();
                    if(map.containsKey(num)){
                        map.put(num,2);
                    }else {
                        map.put(num,1);
                    }
                    //System.out.println(generaterOrderCode());
                }
            }).start();
        }

        for (Object obj : map.values()) {
            if((int)obj != 1) {
                System.out.println("有重复");
            } else {
                System.out.println("无重复");
            }
        }
    }
}
