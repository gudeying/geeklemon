package cn.geekelmon.app.api.config;

import cn.geeklemon.core.bean.factory.InitializingBean;
import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.core.context.annotation.Value;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import redis.clients.jedis.*;

import java.util.LinkedList;
import java.util.List;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/12/24 15:51
 * Modified by : kavingu
 */
public class RedisConfig implements InitializingBean {
    @Value(name = "lemon.data.redis.host", defaultValue = "127.0.0.1")
    private String redisHost;
    @Value(name = "lemon.data.redis.port", defaultValue = "6379")
    private String redisPort;
    @Value(name = "lemon.data.redis.password", defaultValue = "geeklemon-redis-2019")
    private String redisPassword;
    private int connectTimeOut = 2000;
    private int soTimeOut = 2000;
    private int dataBase = 0;
    private String clientName = "geeklemon";
    private boolean useSSL = false;


    private JedisPool jedisPool;

    //    @Bean
    private JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxTotal(50);// 最大连接数，连接全部用完，进行等待

        poolConfig.setMinIdle(10); // 最小空余数

        poolConfig.setMaxIdle(30); // 最大空余数

        JedisPool pool = new JedisPool(
                poolConfig,
                redisHost,
                NumberUtil.parseInt(redisPort),
                connectTimeOut,
                soTimeOut,
                redisPassword,
                dataBase,
                clientName, useSSL, null, null, null);

        return pool;
    }

    public static void main(String[] args) {
        RedisConfig redisConfig = new RedisConfig();
        JedisPool pool = redisConfig.jedisPool();
        Jedis jedis = pool.getResource();
        jedis.lpushx("");
        jedis.close();

    }

    @Override
    public void afterPropsSet() {
        this.jedisPool = jedisPool();
    }
}
