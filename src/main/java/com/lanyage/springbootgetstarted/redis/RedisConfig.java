package com.lanyage.springbootgetstarted.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableCaching
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private Integer port;
    @Value("${spring.redis.timeout}")
    private Integer timeout;

    //自定义缓存key生成策略
    //@Bean
    //public KeyGenerator keyGenerator() {
    //    return new KeyGenerator(){
    //        @Override
    //        public Object generate(Object target, java.lang.reflect.Method method, Object... params) {
    //            StringBuffer sb = new StringBuffer();
    //            sb.append(target.getClass().getName());
    //            sb.append(method.getName());
    //            for(Object obj:params){
    //                sb.append(obj.toString());
    //            }
    //            return sb.toString();
    //        }
    //    };
    //}

    /**以下是两种配置RedisCacheManager的方法*/

//    @Bean(name = "redisManager")
//    public CacheManager cacheManager(RedisConnectionFactory factory) {
//        RedisCacheManager cacheManager = RedisCacheManager.create(factory);
//        return cacheManager;
//    }

    @Bean(name = "redisManager")
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        logger.info("==> [{CacheManager}] has been initialized. <==");
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();

        configuration = configuration
                        .entryTtl(Duration.ofMinutes(1))   //设置缓存的默认过期时间
                        .disableCachingNullValues();       //不缓存空值
        //设置一个初始化的缓存空间set集合
        Set<String> cacheNames = new HashSet<>();
        cacheNames.add("users");
        cacheNames.add("redis-cache-secondary");

        //对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();

        configurationMap.put("users", configuration.entryTtl(Duration.ofSeconds(600)));
        configurationMap.put("redis-cache-secondary", configuration.entryTtl(Duration.ofSeconds(120)));

        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                .initialCacheNames(cacheNames)  //一定要先执行这个缓存名字的设置
                .withInitialCacheConfigurations(configurationMap)
                .build();
        return cacheManager;
    }
}
