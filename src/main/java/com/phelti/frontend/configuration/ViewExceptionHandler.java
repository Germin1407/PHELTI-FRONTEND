package com.phelti.frontend.configuration;

import com.phelti.frontend.Dtos.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

@ControllerAdvice
public class ViewExceptionHandler {

    @ExceptionHandler(RestClientResponseException.class)
    public String errorDelBackend(RestClientResponseException exception, Model model,
                                  HttpServletResponse response, HttpSession session) {
        model.addAttribute(SessionModelAdvice.USUARIO_ACTIVO_ID, session.getAttribute(SessionModelAdvice.USUARIO_ACTIVO_ID));
        model.addAttribute(SessionModelAdvice.USUARIO_ACTIVO_NOMBRE, session.getAttribute(SessionModelAdvice.USUARIO_ACTIVO_NOMBRE));
        response.setStatus(exception.getStatusCode().value());
        String mensaje = "No se pudo completar la solicitud. Inténtalo nuevamente.";
        try {
            ErrorResponse error = exception.getResponseBodyAs(ErrorResponse.class);
            if (error != null && error.getMessage() != null && !error.getMessage().isBlank()) {
                mensaje = error.getMessage();
            }
        } catch (RuntimeException ignored) {

        }
        model.addAttribute("codigoEstado", exception.getStatusCode().value());
        model.addAttribute("mensaje", mensaje);
        return "errores/error";
    }

    @ExceptionHandler(ResourceAccessException.class)
    public String backendNoDisponible(ResourceAccessException exception, Model model,
                                     HttpServletResponse response, HttpSession session) {
        model.addAttribute(SessionModelAdvice.USUARIO_ACTIVO_ID, session.getAttribute(SessionModelAdvice.USUARIO_ACTIVO_ID));
        model.addAttribute(SessionModelAdvice.USUARIO_ACTIVO_NOMBRE, session.getAttribute(SessionModelAdvice.USUARIO_ACTIVO_NOMBRE));
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        model.addAttribute("codigoEstado", HttpStatus.SERVICE_UNAVAILABLE.value());
        model.addAttribute("mensaje", "El servicio no está disponible en este momento. Inténtalo nuevamente más tarde.");
        return "errores/error";
    }
}
