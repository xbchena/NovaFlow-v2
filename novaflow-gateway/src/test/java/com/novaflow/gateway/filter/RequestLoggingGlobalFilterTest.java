package com.novaflow.gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class RequestLoggingGlobalFilterTest {

    @Test
    void generatesTraceId_andAttachesAsHeader() {
        MockServerHttpRequest req = MockServerHttpRequest.get("/api/v1/user/info").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(req);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        RequestLoggingGlobalFilter filter = new RequestLoggingGlobalFilter();

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        assertThat(traceId).isNotBlank();
        verify(chain).filter(argThat(e ->
                traceId.equals(e.getRequest().getHeaders().getFirst("X-Trace-Id"))));
    }
}
