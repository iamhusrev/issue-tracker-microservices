package com.iamhusrev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(GatewayServerApplication.class,args);
    }

    //    @Bean
//    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route(p -> p
//                        .path("/iamhusrev/user/**")
//                        .filters(f -> f.rewritePath("/iamhusrev/user/(?<segment>.*)","/${segment}")
//                                .addResponseHeader("X-Response-Time",new Date().toString()))
//                        .uri("lb://user-service"))
//                .route(p -> p
//                        .path("/iamhusrev/project/**")
//                        .filters(f -> f.rewritePath("/iamhusrev/project/(?<segment>.*)","/${segment}")
//                                .addResponseHeader("X-Response-Time",new Date().toString()))
//                        .uri("lb://project-service"))
//                .route(p -> p
//                        .path("/iamhusrev/task/**")
//                        .filters(f -> f.rewritePath("/iamhusrev/task/(?<segment>.*)","/${segment}")
//                                .addResponseHeader("X-Response-Time",new Date().toString()))
//                        .uri("lb://task-service"))
//                .build();
//    }
}
