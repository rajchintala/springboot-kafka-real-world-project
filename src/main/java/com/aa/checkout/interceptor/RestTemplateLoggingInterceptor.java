package com.aa.checkout.interceptor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import static com.aa.checkout.util.CheckoutLogPattern.logPattern;

public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RestTemplateLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        log.debug(logPattern("===========================request begin================================================"));
        log.debug(logPattern("URI         : {}"), request.getURI());
        log.debug(logPattern("Method      : {}"), request.getMethod());
        log.debug(logPattern("Headers     : {}"), request.getHeaders());
        log.debug(logPattern("Request body: {}"), new String(body, StandardCharsets.UTF_8));
        log.debug(logPattern("==========================request end================================================"));
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        log.debug(logPattern("============================response begin=========================================="));
        log.debug(logPattern("Status code  : {}"), response.getStatusCode());
        log.debug(logPattern("Status text  : {}"), response.getStatusText());
        log.debug(logPattern("Headers      : {}"), response.getHeaders());
        log.debug(logPattern("Response body: {}"), (StreamUtils.copyToString(response.getBody(), Charset.defaultCharset())));
        log.debug(logPattern("=======================response end================================================="));
    }
}
