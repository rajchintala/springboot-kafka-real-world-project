package com.aa.checkout.config.rest;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;

public interface RestTemplateHttpConnectionConfig {
    HttpClientConnectionManager buildConnectionManager();

    RequestConfig buildRequestConfig();
}
