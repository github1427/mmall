package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午2:21 2018/7/9
 * @ Description：对 jedis api进行封装
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
            RedisPool.returnResource(jedis);
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
            RedisPool.returnResource(jedis);
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
            RedisPool.returnResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long expire(String key,int exTime){
        Jedis jedis=null;
        Long result=null;
        try {
            jedis=RedisPool.getJedis();
            result=jedis.expire(key,exTime);
        }catch (Exception e){
            log.error("get key:{}",key,e);
            RedisPool.returnResource(jedis);
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
            log.error("get key:{}",key,e);
            RedisPool.returnResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        RedisPoolUtil.set("keytest","valuetest");
        RedisPoolUtil.setEx("demon","demon",60*3);
        String value=RedisPoolUtil.get("keytest");
        RedisPoolUtil.expire("keytest",60*3);
        RedisPoolUtil.del("keytest");
        System.out.println("end");
    }
}
