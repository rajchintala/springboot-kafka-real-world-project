package com.aa.checkout.interceptor;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;

public class WebServiceTemplateLoggingInterceptor implements ClientInterceptor {
    private final Logger logger = LoggerFactory.getLogger(WebServiceTemplateLoggingInterceptor.class);

    @Override
    public boolean handleFault(MessageContext msgContext) {
        String requestXml = logInformation(msgContext.getRequest());
        logger.info("Fault -> printing requestXML: \n{}", requestXml);
        String responseXml = logInformation(msgContext.getResponse());
        logger.info("printing responseXML \n{}", responseXml);

        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception e) throws WebServiceClientException {
        // Do nothing
    }

    @Override
    public boolean handleRequest(MessageContext msgContext) {
        String logMessage = logInformation(msgContext.getRequest());
        logger.info("printing request \n {}", logMessage);
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext msgContext) {
        if (msgContext.hasResponse()) {
            String logMessage = logInformation(msgContext.getResponse());
            logger.info("printing response \n {}", logMessage);
        }
        return true;
    }

    private String logInformation(WebServiceMessage message) {
        String str = "";
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            message.writeTo(out);
            byte[] charData = out.toByteArray();
            str = new String(charData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Could not log request: ", e);
        }
        return str;
    }

}
