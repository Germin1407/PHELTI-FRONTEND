package com.phelti.frontend.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.UUID;

@Configuration
public class FormSecurityConfig implements WebMvcConfigurer {
    private static final String TOKEN = "pheltiFormToken";

    @Bean
    public RequestDataValueProcessor requestDataValueProcessor() {
        return new RequestDataValueProcessor() {
            public String processAction(HttpServletRequest request, String action, String method) { request.setAttribute("pheltiFormMethod", method); return action; }
            public String processFormFieldValue(HttpServletRequest request, String name, String value, String type) { return value; }
            public String processUrl(HttpServletRequest request, String url) { return url; }
            public Map<String, String> getExtraHiddenFields(HttpServletRequest request) {
                if ("GET".equalsIgnoreCase(String.valueOf(request.getAttribute("pheltiFormMethod")))) return Map.of();
                var session = request.getSession();
                synchronized (session) {
                    if (session.getAttribute(TOKEN) == null) session.setAttribute(TOKEN, UUID.randomUUID().toString());
                }
                return Map.of("_csrf", session.getAttribute(TOKEN).toString());
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if (!"POST".equals(request.getMethod())) return true;
                var session = request.getSession(false);
                Object expected = session == null ? null : session.getAttribute(TOKEN);
                String sent = request.getParameter("_csrf");
                if (expected instanceof String token && sent != null && MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8), sent.getBytes(StandardCharsets.UTF_8))) return true;
                response.setStatus(403);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("La sesión del formulario ha caducado. Regresa a la página e inténtalo otra vez.");
                return false;
            }
        });
    }
}
