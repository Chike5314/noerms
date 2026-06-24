package com.noerms.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * RedisConfig — only activates when spring.cache.type=redis
 * By default, spring.cache.type=none so this class does nothing.
 * To enable Redis caching:
 *   1. Add spring-boot-starter-data-redis to pom.xml
 *   2. Set spring.cache.type=redis in application.properties
 *   3. Configure spring.data.redis.host/port/password
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisConfig {
    // Redis cache manager is auto-configured by Spring Boot
    // when spring-boot-starter-data-redis is on the classpath
    // and spring.cache.type=redis is set
}
