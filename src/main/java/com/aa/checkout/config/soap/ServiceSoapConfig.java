//package com.aa.checkout.config.soap;
//
//import java.util.concurrent.TimeUnit;
//
//import javax.net.ssl.SSLContext;
//import javax.xml.soap.MessageFactory;
//
//import org.apache.http.HeaderElement;
//import org.apache.http.HeaderElementIterator;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.config.Registry;
//import org.apache.http.config.RegistryBuilder;
//import org.apache.http.conn.ConnectionKeepAliveStrategy;
//import org.apache.http.conn.socket.ConnectionSocketFactory;
//import org.apache.http.conn.socket.PlainConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.apache.http.message.BasicHeaderElementIterator;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.ssl.SSLContextBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.oxm.jaxb.Jaxb2Marshaller;
//import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.ws.client.core.WebServiceTemplate;
//import org.springframework.ws.client.support.interceptor.ClientInterceptor;
//import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
//import org.springframework.ws.transport.http.HttpComponentsMessageSender;
//import org.springframework.ws.transport.http.HttpComponentsMessageSender.RemoveSoapHeadersInterceptor;
//
//import com.aa.checkout.interceptor.WebServiceTemplateLoggingInterceptor;
//
//@Configuration
//@EnableScheduling
//public class ServiceSoapConfig {
//
//    private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30;
//    private final Logger logger = LoggerFactory.getLogger(ServiceSoapConfig.class);
//
//    private final EsoaHttpConfig esoaHttpConfig;
//    private final SoapConnectionConfig soapConnectionConfig;
//    private final TwoWaySSL twoWaySSL;
//
//    @Autowired
//    public ServiceSoapConfig(EsoaHttpConfig esoaHttpConfig,
//                             SoapConnectionConfig soapConnectionConfig,
//                             TwoWaySSL twoWaySSL) {
//        this.esoaHttpConfig = esoaHttpConfig;
//        this.soapConnectionConfig = soapConnectionConfig;
//        this.twoWaySSL = twoWaySSL;
//    }
//
//    private Jaxb2Marshaller getJaxbMarshaller(String... contextPaths) throws Exception {
//        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
//        marshaller.setContextPaths(contextPaths);
//        marshaller.afterPropertiesSet();
//        return marshaller;
//    }
//
//    @Bean(name = "serviceWSTemplate")
//    public WebServiceTemplate getWebserviceTemplate(
//                @Qualifier("serviceMessageSender") HttpComponentsMessageSender messageSender) throws Exception {
//        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory(MessageFactory.newInstance());
//        messageFactory.afterPropertiesSet();
//        WebServiceTemplate webServiceTemplate = new WebServiceTemplate(messageFactory);
//        webServiceTemplate.setMessageSender(messageSender);
//
//        // Use getJaxbMarshaller() for marshaller and unmarshaller if there is a need to set the context path
//        // Jaxb2Marshaller marshaller = getJaxbMarshaller("jaxb.aa.ct.cs.retrieve");
//        XmlBeansMarshaller xmlBeansMarshaller = new XmlBeansMarshaller();
//        webServiceTemplate.setMarshaller(xmlBeansMarshaller);
//        webServiceTemplate.setUnmarshaller(xmlBeansMarshaller);
//
//        webServiceTemplate.setInterceptors(new ClientInterceptor[] {new WebServiceTemplateLoggingInterceptor()});
//        webServiceTemplate.afterPropertiesSet();
//        return webServiceTemplate;
//    }
//
//    @Bean(name = "servicePoolingConnectionManager")
//    public PoolingHttpClientConnectionManager poolingConnectionManager() throws Exception {
//        SSLContext sslContext = new SSLContextBuilder().setProtocol(esoaHttpConfig.getTlsProtocol())
//                    .setKeyManagerFactoryAlgorithm(esoaHttpConfig.getKeyManagerFactoryAlgorithm())
//                .loadKeyMaterial(twoWaySSL.getKeyStore(), twoWaySSL.getPasswordCharArray())
//                .loadTrustMaterial(twoWaySSL.getTruststore(), null)
//                .build();
//        SSLContext.setDefault(sslContext);
//
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
//
//        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
//                    .register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();
//
//        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(
//                    socketFactoryRegistry);
//        poolingConnectionManager.setDefaultMaxPerRoute(soapConnectionConfig.getMaxConnections());
//        poolingConnectionManager.setMaxTotal(soapConnectionConfig.getMaxConnections());
//        return poolingConnectionManager;
//    }
//
//    @Bean(name = "serviceMessageSender")
//    public HttpComponentsMessageSender serviceMessageSender(
//                @Qualifier("serviceConnectionKeepAliveStrategy") ConnectionKeepAliveStrategy connectionKeepAliveStrategy,
//                @Qualifier("servicePoolingConnectionManager") PoolingHttpClientConnectionManager connectionManager)
//                throws Exception {
//        RequestConfig.Builder requestBuilder = RequestConfig.custom().setConnectTimeout(soapConnectionConfig.getConnectionServiceTimeout())
//                    .setConnectionRequestTimeout(soapConnectionConfig.getConnectionManagerTimeout()).setSocketTimeout(soapConnectionConfig.getReadtimeout());
//
//        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager)
//                    .addInterceptorFirst(new RemoveSoapHeadersInterceptor())
//                    .setKeepAliveStrategy(connectionKeepAliveStrategy).setDefaultRequestConfig(requestBuilder.build())
//                    .build();
//
//        return new HttpComponentsMessageSender(httpClient);
//    }
//
//    @Bean(name = "serviceConnectionKeepAliveStrategy")
//    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
//        return new ConnectionKeepAliveStrategy() {
//            @Override
//            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
//                HeaderElementIterator it = new BasicHeaderElementIterator(
//                            response.headerIterator(HTTP.CONN_KEEP_ALIVE));
//                while (it.hasNext()) {
//                    HeaderElement he = it.nextElement();
//                    String param = he.getName();
//                    String value = he.getValue();
//
//                    if (value != null && param.equalsIgnoreCase("timeout")) {
//                        return Long.parseLong(value) * 1000;
//                    }
//                }
//                return soapConnectionConfig.getKeepalive();
//            }
//        };
//    }
//
//    @Bean(name = "serviceIdleConnectionMonitor")
//    public Runnable idleConnectionMonitor(
//                @Qualifier("servicePoolingConnectionManager") PoolingHttpClientConnectionManager connectionManager)
//                throws Exception {
//        return new Runnable() {
//            @Override
//            @Scheduled(fixedDelay = 10000)
//            public void run() {
//                try {
//                    if (connectionManager != null) {
//                        logger.trace("run IdleConnectionMonitor - Closing expired and idle connections...");
//                        connectionManager.closeExpiredConnections();
//                        connectionManager.closeIdleConnections(CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS, TimeUnit.SECONDS);
//                    } else {
//                        logger.trace("run IdleConnectionMonitor - Http Client Connection manager is not initialised");
//                    }
//                } catch (Exception e) {
//                    logger.error("run IdleConnectionMonitor - Exception occurred. msg={}, e={}", e.getMessage(), e);
//                }
//            }
//        };
//    }
//
//}
