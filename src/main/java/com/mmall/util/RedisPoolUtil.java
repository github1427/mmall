package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午3:42 2018/7/11
 * @ Description：
 */
@Slf4j
public class RedisPoolUtil {
    public static String set(String key,String value){
        Jedis jedis=null;
        String result=null;
        try {
            jedis=RedisPool.getJedis();
            result=jedis.set(key,value);
        }catch (Exception e){
            log.error("set key:{} value:{} error",key,value,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }


    //exTime 的时间单位是秒
    public static String setEx(String key,String value,int exTime){
        Jedis jedis=null;
        String result=null;
        try {
            jedis=RedisPool.getJedis();
            result=jedis.setex(key,exTime,value);
        }catch (Exception e){
            log.error("setEx key:{} value:{} time:{} error",key,value,exTime,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key){
        Jedis jedis=null;
        String result=null;
        try {
            jedis=RedisPool.getJedis();
            result=jedis.get(key);
        }catch (Exception e){
            log.error("get key:{}",key,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long expire(String key,int exTime){
        Jedis jedis=null;
        Long result=null;
        try {
            jedis= RedisPool.getJedis();
            result=jedis.expire(key,exTime);
        }catch (Exception e){
            log.error("expire key:{} exTime:{}",key,exTime,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key){
        Jedis jedis=null;
        Long result=null;
        try {
            jedis=RedisPool.getJedis();
            result=jedis.del(key);
        }catch (Exception e){
            log.error("del key:{}",key,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        for (int i=0;i<10;i++){
            RedisShardedPoolUtil.set("key"+i,"value"+i);
        }
        System.out.println("end");
    }
}
