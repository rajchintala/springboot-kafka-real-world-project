package com.aa.checkout.config.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class RestConnectionConfig {
    @Value("${checkout.service.connect.timeout.milliseconds}")
    private int connectTimeout;

    @Value("${checkout.service.socket.timeout.milliseconds}")
    private int socketTimeout;

    @Value("${checkout.service.connectionManagerTimeout.milliseconds}")
    private int connectionManagerTimeout;

    @Value("${checkout.service.maxConnections}")
    private int maxConnections;

    @Value("${checkout.service.connection.timetolive.milliseconds}")
    private int connectionTimeToLive;
}
