package com.aa.checkout;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SpringBootProducerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringBootProducerApplication.class)
                    .sources(SpringBootProducerApplication.class)
                    .properties(getProperties())
                    .run(args);
    }

    static Properties getProperties() {
        Properties props = new Properties();
        props.put("spring.profiles.active", "${ENV}");
        return props;
    }

}
