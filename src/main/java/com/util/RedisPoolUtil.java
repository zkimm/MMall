package com.util;

import com.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

import java.util.List;

@Slf4j
public class RedisPoolUtil {


    public static String set(String key, String value) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key: {} value: {} error", key, value, e);
            RedisShardedPool.returnResource(jedis);
            return result;
        }
        return result;
    }

    /**
     * 单位时秒
     * @param key
     * @param value
     * @param exTime
     * @return
     */
    public static String setEx(String key, String value,int exTime) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setex(key,exTime, value);
        } catch (Exception e) {
            log.error("setex key: {} value: {} error", key, value, e);
            RedisShardedPool.returnResource(jedis);
            return result;
        }
        return result;
    }

    /**
     * 重新设置key的有效期
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key, int exTime) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire key: {} error", key, e);
            RedisShardedPool.returnResource(jedis);
            return result;
        }
        return result;
    }

    public static String get(String key) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key: {}  error", key, e);
            RedisShardedPool.returnResource(jedis);
            return result;
        }
        return result;
    }

    public static Long del(String key) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key: {}  error", key, e);
            RedisShardedPool.returnResource(jedis);
            return result;
        }
        return result;
    }

    public static void delList(List<String> keyList){
        for (String s:keyList){
            del(s);
        }
    }

}
