package com.grinnotech.patients.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import com.grinnotech.patients.SpringBootPersistenceApplication;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import org.junit.Ignore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = SpringBootPersistenceApplication.class)
@DataMongoTest
@ExtendWith(SpringExtension.class)
@Ignore
public class MongoDbSpringIntegrationTest {
    @DisplayName("Given object When save object using MongoDB template Then object can be found")
    @Test
    public void test(MongoTemplate mongoTemplate) {
        // given
        DBObject objectToSave = BasicDBObjectBuilder.start()
            .add("key", "value")
            .get();

        // when
        mongoTemplate.save(objectToSave, "collection");

        // then
        assertThat(mongoTemplate.findAll(DBObject.class, "collection")).extracting("key")
            .containsOnly("value");
    }
}
