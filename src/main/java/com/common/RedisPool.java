package com.common;

import com.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static JedisPool pool;
    private static Integer port=Integer.parseInt(PropertiesUtil.getProperty("redis.port","6379"));
    private static String redisIp=PropertiesUtil.getProperty("redis.ip");
    private static String password=PropertiesUtil.getProperty("redis.password");
    //最大链接数
    private static Integer maxTotal=Integer.parseInt( PropertiesUtil.getProperty("redis.max.total","20"));
    //在RedisShardedPool中最大的idle状态（空闲）的redis实例的个数
    private static Integer maxIdle=Integer.parseInt( PropertiesUtil.getProperty("redis.max.idle","15"));
    //在RedisShardedPool中最小的idle状态（空闲）的redis实例的个数
    private static Integer minIdle=Integer.parseInt( PropertiesUtil.getProperty("redis.min.total","2"));
    //获取实例前是否先验证
    private static Boolean testOnBorrow=Boolean.parseBoolean( PropertiesUtil.getProperty("redis.test.borrow","true"));
    //放回jedis实例是否先验证
    private static Boolean testOnReturn=Boolean.parseBoolean( PropertiesUtil.getProperty("redis.test.return","true"));


    private static void initPool(){
        JedisPoolConfig config=new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        //链接耗尽时，是否阻塞，true为阻塞，false会抛出异常
        config.setBlockWhenExhausted(true);
        pool=new JedisPool(config,redisIp,port,1000*2,password);
    }

    static {
        initPool();
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }

    public static void returnResource(Jedis jedis){
        if (jedis!=null){
            jedis.close();
        }
    }
}
