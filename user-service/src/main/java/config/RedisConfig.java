package config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 配置 RedisTemplate（解决序列化乱码）
 */
@Configuration
public class RedisConfig {

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class) // 只在 factory 存在时创建
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 如果 factory 为 null，会给出明确错误
        if (factory == null) {
            throw new IllegalStateException("RedisConnectionFactory is not available. Please check Redis configuration.");
        }
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // JSON 序列化
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    // 提供一个可选的 RedisTemplate，Redis 不可用时使用
    @Bean
    @Primary
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedisTemplate<String, Object> disabledRedisTemplate() {
        // 返回一个空操作的 RedisTemplate
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置一个假的 connection factory 避免 NPE
        template.setConnectionFactory(new LettuceConnectionFactory());
        return template;
    }
}
