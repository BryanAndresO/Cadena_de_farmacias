package com.espe.gateway.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reactivestreams.Publisher;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class GatewayErrorFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GatewayErrorFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public int getOrder() {
        return -1; // run early
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        ServerHttpResponseDecorator decorated = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                HttpStatusCode statusCode = getStatusCode();
                if (statusCode != null && statusCode.value() >= 400) {
                    try {
                        ApiError apiError = new ApiError(statusCode.value(), "GATEWAY_ERROR",
                                "No fue posible procesar la solicitud. Intente nuevamente más tarde.", null);
                        byte[] b = objectMapper.writeValueAsBytes(apiError);
                        DataBuffer buffer = bufferFactory.wrap(b);
                        getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        getHeaders().setContentLength(b.length);
                        log.warn("Gateway normalizó respuesta de error con status={}", statusCode.value());
                        return super.writeWith(Mono.just(buffer));
                    } catch (Exception e) {
                        log.error("Error serializando ApiError en gateway: {}", e.getMessage(), e);
                    }
                }
                return super.writeWith(body);
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };

        return chain.filter(exchange.mutate().response(decorated).build());
    }
}
