package com.test;

import redis.clients.jedis.Jedis;

public class test_redis {
    public static void main(String[] args) {
        Jedis jedis=new Jedis("192.168.25.138",6379);
        jedis.auth("104615");
        System.out.print(jedis.ping());
    }
}
