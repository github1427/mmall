package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午3:14 2018/7/11
 * @ Description：
 */
public class RedisShardedPool {
    private static ShardedJedisPool pool;
    private static Integer maxTotal = Integer.valueOf(PropertiesUtil.getProperty("redis.maxActive", "20")); //可用连接实例的最大数目，默认为8，若设置-1则表示不限制连接实例数
    private static Integer maxIdle = Integer.valueOf(PropertiesUtil.getProperty("redis.maxIdle", "10"));//在jedispool中最大的状态为idle(空闲)的jedis实例数目，默认为8
    private static Integer minIdle = Integer.valueOf(PropertiesUtil.getProperty("redis.minIdle", "2"));//在jedispool中最小的状态为idle(空闲)的jedis实例数目，默认是0
    private static boolean testOnBorrow = Boolean.valueOf(PropertiesUtil.getProperty("redis.testOnBorrow", "true"));//在borrow一个jedis实例时候，是否进行验证，若赋值为true，则保证拿到的jedis实例肯定能是可用的。
    private static boolean testOnReturn = Boolean.valueOf(PropertiesUtil.getProperty("redis.testOnReturn", "false"));//在return一个jedis实例时候，是否进行验证，若赋值为true，则保证放回的jedis实例肯定是可用的。
    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.valueOf(PropertiesUtil.getProperty("redis1.port"));
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redi21Port = Integer.valueOf(PropertiesUtil.getProperty("redis2.port"));


    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true);//当连接池中的连接耗尽时是否阻塞，若为true则阻塞到maxWait后，若为false则报出异常
        JedisShardInfo jedisShardInfo1=new JedisShardInfo(redis1Ip,redis1Port);
        JedisShardInfo jedisShardInfo2=new JedisShardInfo(redis2Ip,redi21Port);
        List<JedisShardInfo> list=new ArrayList<>(2);
        list.add(jedisShardInfo1);
        list.add(jedisShardInfo2);
        pool=new ShardedJedisPool(config,list,Hashing.MURMUR_HASH,Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
        initPool();
    }

    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis) {
        pool.returnBrokenResource(jedis);
    }

}
