package com.example.notes.config;

import com.example.notes.converter.ZonedDateTimeReadConverter;
import com.example.notes.converter.ZonedDateTimeWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Arrays;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new ZonedDateTimeReadConverter(),
                new ZonedDateTimeWriteConverter()
        ));
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory, MongoMappingContext context, MongoCustomConversions conversions) {
        DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, context);
        converter.setCustomConversions(conversions);
        return converter;
    }

    //@Bean enable when replica set is enabled
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

}