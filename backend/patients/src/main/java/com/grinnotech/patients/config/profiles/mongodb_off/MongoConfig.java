package com.grinnotech.patients.config.profiles.mongodb_off;

//import com.mongodb.MongoClient;
//import com.mongodb.MongoClientURI;
//import com.mongodb.client.MongoDatabase;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.data.mongodb.MongoDbFactory;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
//
//import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
//import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
//
//@Configuration
//@Profile("mongodb")
//public class MongoConfig {
//
//    @Value("${mongodb.uri}")
//    private String mongodbUri;

//    private MongoClientURI createUri() {
//        return new MongoClientURI(mongodbUri);
//    }

//    @Bean
//    public MongoClient mongoClient() {
//        return new MongoClient(createUri());
//    }

//    @Bean
//    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
//        return mongoClient.getDatabase(createUri().getDatabase()).withCodecRegistry(
//                fromRegistries(MongoClient.getDefaultCodecRegistry(),
//                fromProviders(new ListCodec.Provider())));
////                fromProviders(new PojoCodecProvider())));
//    }

//    @Bean
//    public MongoDbFactory mongoDbFactory() throws Exception {
//        return new SimpleMongoDbFactory(createUri());
//    }

//    @Bean
//    public MongoTemplate mongoTemplate() throws Exception {
//        return new MongoTemplate(mongoDbFactory());
//    }
//}
