package com.rsp.learnify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

@Configuration
public class GridFsConfiguration {

    @Bean
    public GridFsTemplate gridFsTemplate(MongoDatabaseFactory mongoDbFactory,
            MappingMongoConverter mappingMongoConverter) {

        return new GridFsTemplate(mongoDbFactory, mappingMongoConverter);

    }

    @Bean
    public GridFSBucket gridFSBucket(MongoClient mongoClient, MongoDatabaseFactory mongoDatabaseFactory) {

        return GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());

    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory mongoDbFactory,
            MongoMappingContext context) {

        DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, context);

        return converter;

    }
}