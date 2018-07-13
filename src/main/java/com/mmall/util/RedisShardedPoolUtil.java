package com.mmall.util;

import com.mmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午2:21 2018/7/9
 * @ Description：对 jedis api进行封装
 */
@Slf4j
public class RedisShardedPoolUtil {

    public static String set(String key,String value){
        ShardedJedis jedis=null;
        String result=null;
        try {
            jedis= RedisShardedPool.getJedis();
            result=jedis.set(key,value);
        }catch (Exception e){
            log.error("set key:{} value:{} error",key,value,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }


    //exTime 的时间单位是秒
    public static String setEx(String key,String value,int exTime){
        ShardedJedis jedis=null;
        String result=null;
        try {
            jedis=RedisShardedPool.getJedis();
            result=jedis.setex(key,exTime,value);
        }catch (Exception e){
            log.error("setEx key:{} value:{} time:{} error",key,value,exTime,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String get(String key){
        ShardedJedis jedis=null;
        String result=null;
        try {
            jedis=RedisShardedPool.getJedis();
            result=jedis.get(key);
        }catch (Exception e){
            log.error("get key:{}",key,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static Long expire(String key,int exTime){
        ShardedJedis jedis=null;
        Long result=null;
        try {
            jedis=RedisShardedPool.getJedis();
            result=jedis.expire(key,exTime);
        }catch (Exception e){
            log.error("expire key:{} exTime:{}",key,exTime,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key){
        ShardedJedis jedis=null;
        Long result=null;
        try {
            jedis=RedisShardedPool.getJedis();
            result=jedis.del(key);
        }catch (Exception e){
            log.error("del key:{}",key,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        RedisShardedPoolUtil.set("keytest","valuetest");
        RedisShardedPoolUtil.setEx("demon","demon",60*3);
        String value= RedisShardedPoolUtil.get("keytest");
        RedisShardedPoolUtil.expire("keytest",60*3);
        RedisShardedPoolUtil.del("keytest");
        System.out.println("end");
    }
}
