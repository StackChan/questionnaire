package com.aim.questionnaire;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableCaching  // 开启缓存
@EnableTransactionManagement // 开启事务，保证redis与mysql中数据的一致性
@EnableAsync
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.aim.questionnaire.dao")
public class MsUserApplication {
	/**
	 * 配置redis不使用springboot默认采用JDK进行序列化的操作，通过以下配置修改为基于Jackson的序列化操作
	 * 整体可读性有了很大提升，并且能将Java对象自动的序列化为JSON字符串，并且查询时能自动把JSON反序列化为Java对象。
	 * 不过，其中记录了序列化时对应的class名称，目的是为了查询时实现自动反序列化。这会带来额外的内存开销。
	 */
	@Bean
	public RedisCacheConfiguration redisCacheConfiguration(){
		//加载redis缓存的默认配置
		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
		configuration = configuration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
		return configuration;
	}
	public static void main(String[] args) {
		SpringApplication.run(MsUserApplication.class, args);
	}
}
