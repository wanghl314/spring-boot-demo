package com.whl.spring.demo.mongodb;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.MongoDriverInformation;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Test {

    public static void main(String[] args) throws Exception {
        String authenticationDatabase = "test";
        String database = "test";
        String host = "127.0.0.1";
        String password = "wanghl314";
        int port = 27017;
        String username = "test";

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings((cluster) -> {
                    cluster.hosts(Collections.singletonList(new ServerAddress(host, port)));
                })
                .credential(MongoCredential.createCredential(username, authenticationDatabase, password.toCharArray()))
                .build();
        MongoDriverInformation driverInformation = MongoDriverInformation.builder().driverName("spring-boot").build();

        MongoClient mongoClient = MongoClients.create(settings, driverInformation);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);

        Resource resource = new ClassPathResource("/nosql/test.txt");
        List<String> sqlList = IOUtils.readLines(resource.getInputStream(), StandardCharsets.UTF_8);

        for (String sql : sqlList) {
            if (!StringUtils.isEmpty(sql)) {
                Document result = mongoDatabase.runCommand(Document.parse(sql));
                System.out.println("result: " + result.toJson());
            }
        }
    }

}
