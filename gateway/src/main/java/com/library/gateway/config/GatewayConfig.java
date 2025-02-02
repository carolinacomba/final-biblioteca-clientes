package com.library.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        // TODO
        /**
         * Completar el retorno de las rutas para las APIS
         * /api/personas/     8081
         * /api/biblioteca/   8082
         */
        return builder.routes()
                .route("personas", r -> r.path("/api/personas/**")
                        .uri("http://localhost:8081"))
                .route("biblioteca", r -> r.path("/api/biblioteca/**")
                        .uri("http://localhost:8082"))
                .build();
    }
}