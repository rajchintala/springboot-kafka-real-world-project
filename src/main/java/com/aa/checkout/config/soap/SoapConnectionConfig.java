//package com.aa.checkout.config.soap;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SoapConnectionConfig {
//
//    @Value("${checkout.service.readtimeout.milliseconds}")
//    private int readtimeout;
//
//    @Value("${checkout.service.connectionServiceTimeout.milliseconds}")
//    private int connectionServiceTimeout;
//
//    @Value("${checkout.service.connectionManagerTimeout.milliseconds}")
//    private int connectionManagerTimeout;
//
//    @Value("${checkout.service.maxConnections}")
//    private int maxConnections;
//
//    @Value("${checkout.service.keepalive.milliseconds}")
//    private long keepalive;
//
//    public int getReadtimeout() {
//        return readtimeout;
//    }
//
//    public int getConnectionServiceTimeout() {
//        return connectionServiceTimeout;
//    }
//
//    public int getConnectionManagerTimeout() {
//        return connectionManagerTimeout;
//    }
//
//    public int getMaxConnections() {
//        return maxConnections;
//    }
//
//    public long getKeepalive() {
//        return keepalive;
//    }
//}
