package com.aa.checkout.config.rest;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.aa.checkout.exception.RestTemplateCreationException;

@Configuration
public class ServiceRestTemplateHttpConnectionConfig implements RestTemplateHttpConnectionConfig {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final RestConnectionConfig restConnectionConfig;
    private final ServiceHTTPConfig serviceHTTPConfig;

    public ServiceRestTemplateHttpConnectionConfig(RestConnectionConfig restConnectionConfig,
                                                   ServiceHTTPConfig serviceHTTPConfig) {
        this.restConnectionConfig = restConnectionConfig;
        this.serviceHTTPConfig = serviceHTTPConfig;
    }

    @Override
    public HttpClientConnectionManager buildConnectionManager() {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", buildSslSocketFactory())
                    .build();
        PoolingHttpClientConnectionManager poolManager = new PoolingHttpClientConnectionManager(reg);
        poolManager.setMaxTotal(restConnectionConfig.getMaxConnections());
        poolManager.setDefaultMaxPerRoute(restConnectionConfig.getMaxConnections());
        poolManager.setValidateAfterInactivity(restConnectionConfig.getConnectionTimeToLive());
        return poolManager;
    }

    @Override
    public RequestConfig buildRequestConfig() {
        return RequestConfig.custom()
                    .setSocketTimeout(restConnectionConfig.getSocketTimeout())
                    .setConnectTimeout(restConnectionConfig.getConnectTimeout())
                    .setConnectionRequestTimeout(restConnectionConfig.getConnectionManagerTimeout())
                    .build();
    }

    private SSLConnectionSocketFactory buildSslSocketFactory() {
        SSLContext sslContext;
        try {
            sslContext = new SSLContextBuilder().setProtocol(serviceHTTPConfig.getTlsProtocol()).build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LOGGER.error("Error creating SSL Context: " + e.getMessage());
            throw new RestTemplateCreationException(e.getMessage(), e);
        }
        return new SSLConnectionSocketFactory(sslContext, null, null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());
    }
}
