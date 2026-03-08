package com.java.demo.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {

    private static JedisPool jedisPool;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(50);
        config.setMinIdle(10);
        jedisPool = new JedisPool(config, "localhost", 6379, 3000);
    }

    public static Jedis getJedis() {
        try {
            return jedisPool.getResource();
        } catch (Exception e) {
            System.err.println("Redis连接失败: " + e.getMessage());
            return null;
        }
    }

    public static void set(String key, String value) {
        try (Jedis jedis = getJedis()) {
            if (jedis != null) {
                jedis.set(key, value);
            }
        }
    }

    public static void set(String key, String value, int expireSeconds) {
        try (Jedis jedis = getJedis()) {
            if (jedis != null) {
                jedis.setex(key, expireSeconds, value);
            }
        }
    }

    public static String get(String key) {
        try (Jedis jedis = getJedis()) {
            if (jedis != null) {
                return jedis.get(key);
            }
        }
        return null;
    }

    public static void del(String key) {
        try (Jedis jedis = getJedis()) {
            if (jedis != null) {
                jedis.del(key);
            }
        }
    }

    public static void expire(String key, int seconds) {
        try (Jedis jedis = getJedis()) {
            if (jedis != null) {
                jedis.expire(key, seconds);
            }
        }
    }

    public static boolean exists(String key) {
        try (Jedis jedis = getJedis()) {
            if (jedis != null) {
                return jedis.exists(key);
            }
        }
        return false;
    }

    public static Long ttl(String key) {
        try (Jedis jedis = getJedis()) {
            if (jedis != null) {
                return jedis.ttl(key);
            }
        }
        return -1L;
    }

    public static void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }
}
