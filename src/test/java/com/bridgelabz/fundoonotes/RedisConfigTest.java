package com.bridgelabz.fundoonotes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest(properties = {
        "spring.profiles.active=redis",
        "spring.datasource.url=jdbc:h2:mem:redis_config_test;DB_CLOSE_DELAY=-1"
})
class RedisConfigTest {
    @Autowired CacheManager cacheManager;

    @Test
    void redisProfileUsesRedisCacheManager() {
        assertInstanceOf(RedisCacheManager.class, cacheManager);
    }
}
