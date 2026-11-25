package com.phelti.frontend.configuration;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SessionModelAdvice {

    private final com.phelti.frontend.webservicesclients.UserApiClient usuarios;

    public SessionModelAdvice(com.phelti.frontend.webservicesclients.UserApiClient usuarios) {
        this.usuarios = usuarios;
    }

    @ModelAttribute("cuentasDemo")
    public java.util.List<com.phelti.frontend.Dtos.UserResponse> cuentasDemo() {
        try { return usuarios.listar(); }
        catch (com.phelti.frontend.webservicesclients.BackendApiException exception) { return java.util.List.of(); }
    }

    public static final String USUARIO_ACTIVO_ID = "usuarioActivoId";
    public static final String USUARIO_ACTIVO_NOMBRE = "usuarioActivoNombre";

    @ModelAttribute(USUARIO_ACTIVO_ID)
    public Integer usuarioActivoId(HttpSession session) {
        Object value = session.getAttribute(USUARIO_ACTIVO_ID);
        return value instanceof Integer id ? id : null;
    }

    @ModelAttribute(USUARIO_ACTIVO_NOMBRE)
    public String usuarioActivoNombre(HttpSession session) {
        Object value = session.getAttribute(USUARIO_ACTIVO_NOMBRE);
        return value instanceof String nombre ? nombre : null;
    }
}
