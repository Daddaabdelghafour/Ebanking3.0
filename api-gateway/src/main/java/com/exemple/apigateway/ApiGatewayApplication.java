package com.exemple.apigateway;

import org. springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ApiGatewayApplication {

    public static void main(String[] args) {
        //21:32
        SpringApplication. run(ApiGatewayApplication.class, args);
    }
}
