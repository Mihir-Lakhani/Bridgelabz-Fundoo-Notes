package com.bridgelabz.fundoonotes.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("redis")
@EnableCaching
public class RedisCacheConfig { }
