package com.aa.checkout.config.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceHTTPConfig {
    @Value("${checkout.service.tlsProtocol}")
    private String tlsProtocol;

    public String getTlsProtocol() {
        return tlsProtocol;
    }
}
