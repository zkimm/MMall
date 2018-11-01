package com.util;

import com.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.List;

@Slf4j
public class RedisPoolUtil {


    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key: {} value: {} error", key, value, e);
            RedisPool.returnResource(jedis);
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
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key,exTime, value);
        } catch (Exception e) {
            log.error("setex key: {} value: {} error", key, value, e);
            RedisPool.returnResource(jedis);
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
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire key: {} error", key, e);
            RedisPool.returnResource(jedis);
            return result;
        }
        return result;
    }

    public static String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key: {}  error", key, e);
            RedisPool.returnResource(jedis);
            return result;
        }
        return result;
    }

    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key: {}  error", key, e);
            RedisPool.returnResource(jedis);
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
