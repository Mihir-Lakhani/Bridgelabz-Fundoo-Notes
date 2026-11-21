package com.bridgelabz.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class FundooGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FundooGatewayApplication.class, args);
    }

    @Bean
    RouteLocator routes(RouteLocatorBuilder builder,
                        @Value("${fundoo.backend.url:http://localhost:8080}") String backendUrl) {
        return builder.routes()
                .route("fundoo-api", route -> route.path("/api/**").uri(backendUrl))
                .build();
    }
}
