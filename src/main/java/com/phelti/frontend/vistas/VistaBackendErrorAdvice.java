package com.phelti.frontend.vistas;

import com.phelti.frontend.webservicesclients.BackendApiException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.phelti.frontend.configuration.SessionModelAdvice;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class VistaBackendErrorAdvice {
    @ExceptionHandler(BackendApiException.class)
    public String mostrarError(BackendApiException exception, Model model,
                               HttpServletResponse response, HttpSession session) {
        model.addAttribute(SessionModelAdvice.USUARIO_ACTIVO_ID, session.getAttribute(SessionModelAdvice.USUARIO_ACTIVO_ID));
        model.addAttribute(SessionModelAdvice.USUARIO_ACTIVO_NOMBRE, session.getAttribute(SessionModelAdvice.USUARIO_ACTIVO_NOMBRE));
        response.setStatus(exception.getStatusCode());
        model.addAttribute("codigoEstado", exception.getStatusCode());
        model.addAttribute("mensaje", exception.getMessage());
        return "errores/error";
    }
}
