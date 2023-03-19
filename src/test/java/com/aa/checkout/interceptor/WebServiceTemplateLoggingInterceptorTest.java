package com.aa.checkout.interceptor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;

class WebServiceTemplateLoggingInterceptorTest {
    private WebServiceTemplateLoggingInterceptor webServiceTemplateLoggingInterceptor;
    private MessageContext messageContext;

    @BeforeEach
    public void setUp() {
        webServiceTemplateLoggingInterceptor = new WebServiceTemplateLoggingInterceptor();
        messageContext = Mockito.mock(MessageContext.class);

        WebServiceMessage webServiceMessage = Mockito.mock(WebServiceMessage.class);

        Mockito.when(messageContext.getRequest()).thenReturn(webServiceMessage);
        Mockito.when(messageContext.getResponse()).thenReturn(webServiceMessage);
    }

    @Test
    public void handleFault_shouldReturnTrue() {
        Assertions.assertTrue(webServiceTemplateLoggingInterceptor.handleFault(messageContext));
    }

    @Test
    public void handleRequest_shouldReturnTrue() {
        Assertions.assertTrue(webServiceTemplateLoggingInterceptor.handleRequest(messageContext));
    }

    @Test
    public void handleResponse_shouldReturnTrue() {
        Mockito.when(messageContext.hasResponse()).thenReturn(true);

        Assertions.assertTrue(webServiceTemplateLoggingInterceptor.handleResponse(messageContext));
    }

    @Test
    public void logInformation_whenWebServiceMessageIsNull_shouldThrowException() {
        Mockito.when(messageContext.getRequest()).thenReturn(null);
        Logger logger = Mockito.mock(Logger.class);
        ReflectionTestUtils.setField(webServiceTemplateLoggingInterceptor, "logger", logger);

        webServiceTemplateLoggingInterceptor.handleRequest(messageContext);

        Mockito.verify(logger).error(Mockito.anyString(), Mockito.any(Exception.class));
    }

    @Test
    public void afterCompletion_shouldDoNothing() {
        webServiceTemplateLoggingInterceptor.afterCompletion(messageContext, new Exception());
    }
}