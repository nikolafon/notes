package com.example.notes.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.function.Supplier;

@Configuration
public class RateLimitConfig {

    @Autowired
    private RedisClient redisClient;

    @Bean
    public ProxyManager<byte[]> lettuceBasedProxyManager() {
        StatefulRedisConnection<byte[], byte[]> redisConnection = redisClient
                .connect(ByteArrayCodec.INSTANCE);

        return LettuceBasedProxyManager.builderFor(redisConnection)
                .withExpirationStrategy(
                        ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(1L)))
                .build();
    }

    @Bean
    public Supplier<BucketConfiguration> bucketConfiguration() {
        Refill refill = Refill.intervally(100, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(100, refill);
        return () -> BucketConfiguration.builder()
                .addLimit(limit)
                .build();
    }
}
