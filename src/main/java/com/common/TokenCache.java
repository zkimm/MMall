package com.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.util.RedisPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCache {

    public static final String TOKEN_PREFIX = "token_";
    public static final Integer ANWSER_TIME = 60*60*12;

    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

//    private static LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000)
//            .expireAfterAccess(12, TimeUnit.HOURS)
//            .build(new CacheLoader<String, String>() {
//                //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就调用这个方法
//                @Override
//                public String load(String s) throws Exception {
//                    return "null";
//                }
//            });

    public static void setKey(String key, String value) {
        RedisPoolUtil.setEx(key,value,ANWSER_TIME);
//        loadingCache.put(key, value);
    }

    public static String getKey(String key) {
        String value = null;
        try {
            value = RedisPoolUtil.get(key);
//            value = loadingCache.get(key);
            if ("null".equals(value)) {
                return null;
            }
        } catch (Exception e) {
            logger.error("tokenCache error ", e);
        }
        return value;
    }
}
