package com.aa.checkout.util;

import java.util.ArrayList;
import java.util.List;

public final class CheckoutLogPattern {

    private static final String SERVICE_NAME = "[template-replace:serviceName]";
    private static final String SEPARATOR = " : ";

    private CheckoutLogPattern() {

    }

    public static String logPattern(String message) {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];

        List<String> list = new ArrayList<>();
        list.add(SERVICE_NAME);
        list.add(stackTraceElement.getFileName());
        list.add(stackTraceElement.getMethodName());
        list.add(String.valueOf(stackTraceElement.getLineNumber()));

        String pattern = "[" + String.join(SEPARATOR, list) + "]";
        return pattern + message;
    }
}
