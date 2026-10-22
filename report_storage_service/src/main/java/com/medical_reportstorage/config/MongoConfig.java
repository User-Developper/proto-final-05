package com.medical_reportstorage.config;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;

@Configuration
public class MongoConfig {

    @Bean
    public GridFSBucket gridFSBucket(MongoTemplate mongoTemplate) {
        return GridFSBuckets.create(
                mongoTemplate.getDb(),
                "report_image_files"
        );
    }

    @Bean
    public GridFsTemplate gridFsTemplate(
            MongoTemplate mongoTemplate,
            MongoConverter mongoConverter) {

        return new GridFsTemplate(
                mongoTemplate.getMongoDatabaseFactory(),
                mongoConverter,
                "report_image_files"
        );
    }
}