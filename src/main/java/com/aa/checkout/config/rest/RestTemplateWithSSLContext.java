package com.aa.checkout.config.rest;

import java.util.Collections;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.aa.checkout.interceptor.RestTemplateLoggingInterceptor;

@Component
public class RestTemplateWithSSLContext {

    private RestTemplateHttpConnectionConfig restTemplateHttpConnectionConfig;

    public RestTemplate buildRestTemplateWithTrustedCerts(RestTemplateHttpConnectionConfig restTemplateHttpConnectionConfig) {
        this.restTemplateHttpConnectionConfig = restTemplateHttpConnectionConfig;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new RestTemplateLoggingInterceptor()));
        return createRestTemplate(restTemplate);
    }

    private RestTemplate createRestTemplate(RestTemplate restTemplate) {
        RestTemplate returnRestTemp = restTemplate == null ? new RestTemplate() : restTemplate;
        returnRestTemp.setRequestFactory(createRequestFactory());
        return returnRestTemp;
    }

    private ClientHttpRequestFactory createRequestFactory() {
        return new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(buildHttpClient()));
    }

    private HttpClient buildHttpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create().setKeepAliveStrategy(buildConnectionKeepAliveStrategy())
                    .setConnectionManager(restTemplateHttpConnectionConfig.buildConnectionManager())
                    .setDefaultRequestConfig(restTemplateHttpConnectionConfig.buildRequestConfig())
                    .disableAutomaticRetries()
                    .disableConnectionState()
                    .disableCookieManagement()
                    .useSystemProperties();
        return builder.build();
    }

    private ConnectionKeepAliveStrategy buildConnectionKeepAliveStrategy() {
        return new HttpConnectionKeepAliveStrategy();
    }
}