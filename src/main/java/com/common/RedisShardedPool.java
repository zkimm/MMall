package com.common;

import com.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.Arrays;
import java.util.List;

public class RedisShardedPool {
    private static ShardedJedisPool pool;
    private static String password=PropertiesUtil.getProperty("redis.password");

    private static String redisIp=PropertiesUtil.getProperty("redis.ip");
    private static Integer port=Integer.parseInt(PropertiesUtil.getProperty("redis.port","6379"));

    private static String redisIp2 = PropertiesUtil.getProperty("redis2.ip");
    private static Integer port2=Integer.parseInt(PropertiesUtil.getProperty("redis2.port","6380"));

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
//        pool=new JedisPool(config,redisIp,port,1000*2,password);
        JedisShardInfo jedisShardInfo=new JedisShardInfo(redisIp,port,1000*2);
        jedisShardInfo.setPassword(password);
        JedisShardInfo jedisShardInfo2=new JedisShardInfo(redisIp2,port2,1000*2);
        jedisShardInfo2.setPassword(password);
        List<JedisShardInfo> shardInfos= Arrays.asList(jedisShardInfo,jedisShardInfo2);

        pool=new ShardedJedisPool(config,shardInfos, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis){
        if (jedis!=null){
            jedis.close();
        }
    }

    public static void main(String[] args) {
        ShardedJedis jedis=getJedis();
        for (int i=0;i<10;i++){
            jedis.set("key_"+i,"value_"+i);
        }
        returnResource(jedis);
    }
}
