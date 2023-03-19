package com.aa.checkout.interceptor;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

class RestTemplateLoggingInterceptorTest {

    private RestTemplateLoggingInterceptor restTemplateLoggingInterceptor;

    @BeforeEach
    public void setUp() {
        restTemplateLoggingInterceptor = new RestTemplateLoggingInterceptor();
    }

    @Test
    public void intercept_shouldReturnResponse() throws IOException {

        HttpRequest httpRequest = Mockito.mock(HttpRequest.class);
        ClientHttpRequestExecution clientHttpRequestExecution = Mockito.mock(ClientHttpRequestExecution.class);
        ClientHttpResponse clientHttpResponse = new MockClientHttpResponse(new byte[0], HttpStatus.OK);
        Mockito.when(clientHttpRequestExecution.execute(Mockito.any(), Mockito.any())).thenReturn(clientHttpResponse);

        Assertions.assertNotNull(restTemplateLoggingInterceptor.intercept(httpRequest, new byte[0], clientHttpRequestExecution));
    }
}