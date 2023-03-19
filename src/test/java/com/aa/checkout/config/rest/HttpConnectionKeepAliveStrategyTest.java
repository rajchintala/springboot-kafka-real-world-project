package com.aa.checkout.config.rest;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

class HttpConnectionKeepAliveStrategyTest {
    private HttpConnectionKeepAliveStrategy httpConnectionKeepAliveStrategy;

    @BeforeEach
    public void setUp() {
        httpConnectionKeepAliveStrategy = new HttpConnectionKeepAliveStrategy();
    }

    @Test
    public void getKeepAliveDuration_shouldReturnDefaultValue() {
        BasicStatusLine basicStatusLine = Mockito.mock(BasicStatusLine.class);

        long value = httpConnectionKeepAliveStrategy.getKeepAliveDuration(new BasicHttpResponse(basicStatusLine), new BasicHttpContext());
        Assertions.assertEquals(value, 60000);
    }

    @Test
    public void getKeepAliveDuration_shouldReturnTimeoutValue() {
        BasicHttpResponse basicHttpResponse = getBasicHttpResponse("timeout=30");

        long value = httpConnectionKeepAliveStrategy.getKeepAliveDuration(basicHttpResponse, new BasicHttpContext());
        Assertions.assertEquals(30000, value);
    }

    @Test
    public void getKeepAliveDuration_shouldLogNumberFormatException() {
        BasicHttpResponse basicHttpResponse = getBasicHttpResponse("timeout=aa");

        Logger logger = Mockito.mock(Logger.class);
        ReflectionTestUtils.setField(httpConnectionKeepAliveStrategy, "LOGGER", logger);

        httpConnectionKeepAliveStrategy.getKeepAliveDuration(basicHttpResponse, new BasicHttpContext());

        Mockito.verify(logger).error(Mockito.anyString());
    }

    private BasicHttpResponse getBasicHttpResponse(String headerElements) {
        BasicStatusLine basicStatusLine = Mockito.mock(BasicStatusLine.class);
        BasicHttpResponse basicHttpResponse = new BasicHttpResponse(basicStatusLine);

        BasicHeader basicHeader = new BasicHeader(HTTP.CONN_KEEP_ALIVE, headerElements);

        basicHttpResponse.addHeader(basicHeader);
        return basicHttpResponse;
    }
}