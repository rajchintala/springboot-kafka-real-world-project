package com.aa.checkout.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@RequestMapping
public class ServiceController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String version;

    @Autowired
    public ServiceController(@Value("${version:notFound}") String version) {
        this.version = version;
    }

    @RequestMapping(value = "/version", method = RequestMethod.GET)
    @CircuitBreaker(name = "template-replace-circuitBreaker", fallbackMethod = "versionFallBackMethod")
    public ResponseEntity<String> healthStatus() {
        return new ResponseEntity<>(version, HttpStatus.OK);
    }

    public ResponseEntity<String> versionFallBackMethod(Exception e) {
        logger.error("Exception in versionFallBackMethod ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
    }
}
