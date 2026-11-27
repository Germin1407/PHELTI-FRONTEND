package com.phelti.frontend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppRestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(
            @Value("${phelti.backend.connect-timeout-ms:5000}") int connectTimeout,
            @Value("${phelti.backend.read-timeout-ms:10000}") int readTimeout,
            @Value("${phelti.demo.key:}") String key) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        RestTemplate template = new RestTemplate(factory);
        template.getInterceptors().add((request, body, execution) -> {
            if (!key.isBlank()) request.getHeaders().set("X-Phelti-Key", key);
            var attributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attributes instanceof org.springframework.web.context.request.ServletRequestAttributes servlet) {
                var session = servlet.getRequest().getSession(false);
                if (session != null && session.getAttribute(SessionModelAdvice.USUARIO_ACTIVO_ID) instanceof Integer actor) {
                    request.getHeaders().set("X-Phelti-Actor", actor.toString());
                }
            }
            return execution.execute(request, body);
        });
        return template;
    }
}
