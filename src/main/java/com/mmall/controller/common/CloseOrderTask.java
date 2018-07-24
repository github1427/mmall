package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.common.RedissonManager;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午12:16 2018/7/14
 * @ Description：
 */
@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private RedissonManager redissonManager;

    //@Scheduled(cron = "0 */1 * * * ?")//每一分钟（一分钟的整数倍时候执行）
    public void closeOrderTaskV1(){
        log.info("定时执行任务执行开启");
        iOrderService.closeOrderByHour(Integer.valueOf(PropertiesUtil.getProperty("close.order.task.time","2")));
        log.info("定时执行任务执行结束");
    }

    //分布式锁
    //@Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV2(){
        log.info("定时执行任务执行开启");
        String lockName= Const.RedisLock.CLOSE_ORDER_TASK_LOCK;
        Long lockout= Long.valueOf(PropertiesUtil.getProperty("task.lockout","5000"));
        Long resultLock= RedisShardedPoolUtil.setnx(lockName,String.valueOf(System.currentTimeMillis()+lockout));
        if (resultLock!=null&&resultLock==1){
           closeOrder();
        }else {
            log.info("获取分布式锁:{} 失败",Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("定时执行任务执行结束");
    }
    //@Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV3(){
        log.info("定时执行任务执行开启");
        String lockName= Const.RedisLock.CLOSE_ORDER_TASK_LOCK;
        Long lockout= Long.valueOf(PropertiesUtil.getProperty("task.lockout","5000"));
        Long resultLock= RedisShardedPoolUtil.setnx(lockName,String.valueOf(System.currentTimeMillis()+lockout));
        if (resultLock!=null&&resultLock==1){
            closeOrder();
        }else {
            String lockValueStr=RedisShardedPoolUtil.get(Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
            if (lockValueStr!=null&&System.currentTimeMillis()>Long.parseLong(lockValueStr)){
                String getSetResult=RedisShardedPoolUtil.getSet(lockName,String.valueOf(System.currentTimeMillis()+lockout));
                if (getSetResult==null||(getSetResult!=null&& StringUtils.equals(getSetResult,lockValueStr))){
                    closeOrder();
                }else {
                    log.info("获取分布式锁:{} 失败",Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
                }
            }else {
                log.info("获取分布式锁:{} 失败",Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
            }
        }
        log.info("定时执行任务执行结束");
    }


    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV4(){
        boolean getLock=false;
        RLock rLock=redissonManager.getRedisson().getLock(Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
        try {
            if (getLock=rLock.tryLock(0,5, TimeUnit.SECONDS)){
                log.info("Redisson获取到分布式锁:{} 成功  ThreadName:{}",Const.RedisLock.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
                //iOrderService.closeOrderByHour(Integer.valueOf(PropertiesUtil.getProperty("close.order.task.time","2")));
            }else {
                log.info("Redisson没有获取到分布式锁:{} ThreadName:{}",Const.RedisLock.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            log.error("Redisson获取分布式锁异常",e);
        }finally {
            if (!getLock){
                return;
            }
            rLock.unlock();
            log.info("Redisson释放分布式锁");
        }
    }


    private void closeOrder(){
        RedisShardedPoolUtil.expire(Const.RedisLock.CLOSE_ORDER_TASK_LOCK,500);
        log.info("获取分布式锁:{}  ThreadName:{}",Const.RedisLock.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        iOrderService.closeOrderByHour(Integer.valueOf(PropertiesUtil.getProperty("close.order.task.time","2")));
        RedisShardedPoolUtil.del(Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
        log.info("释放分布式锁:{}  ThreadName:{}",Const.RedisLock.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
    }
}
