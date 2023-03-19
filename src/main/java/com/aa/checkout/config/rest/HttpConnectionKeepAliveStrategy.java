package com.aa.checkout.config.rest;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {
    private final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionKeepAliveStrategy.class);
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int MILLISECONDS_PER_MINUTE = 60 * MILLISECONDS_PER_SECOND;

    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (isNonNullTimeoutParamValue(param, value)) {
                try {
                    return Long.parseLong(value) * MILLISECONDS_PER_SECOND;
                } catch (NumberFormatException ignore) {
                    LOGGER.error(ignore.getMessage());
                }
            }
        }
        return MILLISECONDS_PER_MINUTE;
    }

    protected boolean isNonNullTimeoutParamValue(String param, String value) {
        return (value != null && "timeout".equalsIgnoreCase(param));
    }
}