//package com.aa.checkout.config.soap;
//
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.cert.CertificateException;
//import java.util.Base64;
//
//@Component
//public class TwoWaySSL {
//    private static final Logger LOGGER = LoggerFactory.getLogger(TwoWaySSL.class);
//
//    private final String keyStoreEncodedString;
//    private final String passPhrase;
//    private String encodedTruststore;
//    private KeyStore keyStore;
//    private KeyStore truststore;
//
//    public TwoWaySSL(@Value("${checkout.esoa.https.keystore.resource}") String keyStoreEncodedString,
//                     @Value("${checkout.esoa.https.keystore.password}") String passPhrase,
//                     @Value("${checkout.esoa.https.truststore.resource}") String encodedTruststore) {
//        this.keyStoreEncodedString = keyStoreEncodedString;
//        this.passPhrase = passPhrase;
//        this.encodedTruststore = encodedTruststore;
//        loadCert();
//    }
//
//    private void loadCert() {
//        byte[] decodedKeystoreBytes = Base64.getDecoder().decode(keyStoreEncodedString.getBytes());
//        ByteArrayInputStream keyInputStream = new ByteArrayInputStream(decodedKeystoreBytes);
//
//        try {
//            loadKeystore(keyInputStream, passPhrase);
//            loadTruststore();
//            keyInputStream.close();
//        } catch (Exception e) {
//            LOGGER.error("Unable to load SSL certificates", e);
//        }
//
//    }
//
//    private void loadTruststore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
//        if (StringUtils.isNotEmpty(encodedTruststore)) {
//            byte[] decodedTruststoreBytes = Base64.getDecoder().decode(encodedTruststore.getBytes());
//            ByteArrayInputStream truststoreStream = new ByteArrayInputStream(decodedTruststoreBytes);
//            this.truststore = KeyStore.getInstance(KeyStore.getDefaultType());
//            this.truststore.load(truststoreStream, passPhrase.toCharArray());
//        }
//    }
//
//    private void loadKeystore(InputStream keyStream, String password)
//            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
//        this.keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        char[] charPassword = password.toCharArray();
//        this.keyStore.load(keyStream, charPassword);
//    }
//
//    public KeyStore getKeyStore() {
//        return keyStore;
//    }
//
//    public KeyStore getTruststore() {
//        return truststore;
//    }
//
//    public char[] getPasswordCharArray() {
//        return (passPhrase != null) ? passPhrase.toCharArray() : null;
//    }
//
//}