package com.mmall.common;


import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午4:04 2018/7/24
 * @ Description：redisson
 */
@Component
@Slf4j
public class RedissonManager {

    private Config config=new Config();
    private Redisson redisson=null;

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.valueOf(PropertiesUtil.getProperty("redis1.port"));
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redi21Port = Integer.valueOf(PropertiesUtil.getProperty("redis2.port"));

    public Redisson getRedisson() {
        return redisson;
    }

    @PostConstruct
    private void init(){
        try {
            config.useSingleServer().setAddress(new StringBuilder().append(redis1Ip).append(":").append(redis1Port).toString());
            redisson= (Redisson) Redisson.create(config);
            log.info("初始化 redisson 成功");
        }catch (Exception e){
            log.error("init redisson error",e);
        }
    }
}
