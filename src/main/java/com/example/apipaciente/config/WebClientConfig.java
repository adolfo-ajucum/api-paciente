package com.example.apipaciente.config; // O tu paquete de configuración

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
public class WebClientConfig {

    private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${legacy.api.baseurl}")
    private String legacyApiBaseUrl;

    @Bean // Define este WebClient como el bean a inyectar
    public WebClient legacyApiWebClient(WebClient.Builder webClientBuilder) throws SSLException {
        log.warn("****************************************************************************");
        log.warn("*** ¡ATENCIÓN! Configurando WebClient para DESHABILITAR validación SSL. ***");
        log.warn("*** ¡NO USAR ESTA CONFIGURACIÓN EN PRODUCCIÓN! ***");
        log.warn("****************************************************************************");

        // Configuración para confiar en todos los certificados (INSEGURO)
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE) // Confía en cualquier certificado
                .build();

        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

        return webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(legacyApiBaseUrl)
                .build();
    }
}