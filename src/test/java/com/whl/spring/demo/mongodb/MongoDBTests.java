package com.whl.spring.demo.mongodb;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.MongoDriverInformation;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class MongoDBTests {
    private static MongoDatabase mongoDatabase;

    @BeforeAll
    public static void init() {
        String authenticationDatabase = "test";
        String database = "test";
        String host = "127.0.0.1";
        String password = "test";
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
        mongoDatabase = mongoClient.getDatabase(database);
    }

    @Test
    public void testRunCommand() throws Exception {
        mongoDatabase.getCollection("test").drop();
        Resource resource = new ClassPathResource("/nosql/test.txt");
        List<String> sqlList = IOUtils.readLines(resource.getInputStream(), StandardCharsets.UTF_8);

        for (String sql : sqlList) {
            if (!StringUtils.isEmpty(sql)) {
                Document result = mongoDatabase.runCommand(Document.parse(sql));
                Number ok = (Number) result.get("ok");
                Assertions.assertEquals(ok.intValue(), 1);

                if (ok.intValue() == 1) {
                    System.out.println("OK");
                } else {
                    System.err.println("ERROR - " + result.get("note"));
                }
            }
        }
    }

}
