package com.aa.checkout.config.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import com.aa.checkout.interceptor.RestTemplateLoggingInterceptor;

import static org.mockito.Mockito.mock;

class RestTemplateWithSSLContextTest {

    private RestTemplateWithSSLContext restTemplateWithSSLContext;
    private ServiceRestTemplateHttpConnectionConfig serviceRestTemplateHttpConnectionConfig;

    @BeforeEach
    public void setUp() {
        restTemplateWithSSLContext = new RestTemplateWithSSLContext();
        serviceRestTemplateHttpConnectionConfig = mock(ServiceRestTemplateHttpConnectionConfig.class);
    }

    @Test
    public void buildRestTemplateWithTrustedCert_RequestFactoryNotNull() {
        RestTemplate restTemplate = restTemplateWithSSLContext.buildRestTemplateWithTrustedCerts(serviceRestTemplateHttpConnectionConfig);
        Assertions.assertEquals(restTemplate.getInterceptors().get(0).getClass(), RestTemplateLoggingInterceptor.class);
        Assertions.assertNotNull(restTemplate.getRequestFactory());
    }
}