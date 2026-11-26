package com.phelti.frontend.vistas;

import com.phelti.frontend.Dtos.CategoryResponse;
import com.phelti.frontend.Dtos.UserResponse;
import com.phelti.frontend.configuration.SessionModelAdvice;
import com.phelti.frontend.webservicesclients.BackendApiException;
import com.phelti.frontend.webservicesclients.CategoryApiClient;
import com.phelti.frontend.webservicesclients.UserApiClient;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class VistaSupport {
    private final UserApiClient usuarios;
    private final CategoryApiClient categorias;

    public VistaSupport(UserApiClient usuarios, CategoryApiClient categorias) {
        this.usuarios = usuarios;
        this.categorias = categorias;
    }

    public Integer usuarioActivo(HttpSession sesion) {
        Object id = sesion.getAttribute(SessionModelAdvice.USUARIO_ACTIVO_ID);
        return id instanceof Integer numero ? numero : null;
    }

    public void activar(HttpSession sesion, UserResponse usuario) {
        sesion.setAttribute(SessionModelAdvice.USUARIO_ACTIVO_ID, usuario.getUserId());
        sesion.setAttribute(SessionModelAdvice.USUARIO_ACTIVO_NOMBRE, usuario.getName());
    }

    public void nombres(Model model) {
        usuarios(model);
        categorias(model);
    }

    public void usuarios(Model model) {
        Map<Integer, String> mapa = new LinkedHashMap<>();
        for (UserResponse usuario : usuarios.listar()) {
            mapa.put(usuario.getUserId(), usuario.getName());
        }
        model.addAttribute("usuariosMap", mapa);
    }

    public void categorias(Model model) {
        Map<Integer, String> mapa = new LinkedHashMap<>();
        for (CategoryResponse categoria : categorias.listar()) {
            mapa.put(categoria.getCategoryId(), categoria.getName());
        }
        model.addAttribute("categoriasMap", mapa);
    }

    public String iniciarSesion(RedirectAttributes flash) {
        flash.addFlashAttribute("error", "Selecciona un usuario activo para continuar.");
        return "redirect:/";
    }

    public String error(RedirectAttributes flash, BackendApiException exception, String destino) {
        flash.addFlashAttribute("error", exception.getMessage());
        return "redirect:" + destino;
    }

    public String correcto(RedirectAttributes flash, String mensaje, String destino) {
        flash.addFlashAttribute("exito", mensaje);
        return "redirect:" + destino;
    }

    public String accesoDenegado(RedirectAttributes flash, String mensaje, String destino) {
        flash.addFlashAttribute("error", mensaje);
        return "redirect:" + destino;
    }

    private String etiqueta(String campo) {
        return switch (campo) {
            case "name" -> "el nombre";
            case "email" -> "el correo electrónico";
            case "title" -> "el título";
            case "description" -> "la descripción";
            case "pheltAmount", "amount", "proposedAmount" -> "el importe en phelts";
            case "durationMinutes" -> "la duración en minutos";
            case "activityDate" -> "la fecha y hora";
            case "activityTime" -> "la hora";
            case "location" -> "la ubicación";
            case "virtualLink" -> "el enlace de reunión";
            case "categoryId" -> "la categoría";
            case "publicationType" -> "el tipo de publicación";
            case "interactionMode", "preferredInteractionMode" -> "la modalidad";
            case "score" -> "la puntuación";
            case "comment" -> "el comentario";
            case "message" -> "el mensaje";
            case "dayOfWeek" -> "el día de la semana";
            case "startTime", "endTime" -> "el horario";
            default -> "los datos del formulario";
        };
    }

    public boolean formularioInvalido(BindingResult resultado, RedirectAttributes flash,
                                      String nombreFormulario, String... camposDelServidor) {
        java.util.Set<String> omitidos = java.util.Set.of(camposDelServidor);
        String erroresCampos = resultado.getFieldErrors().stream()
                .filter(e -> !omitidos.contains(e.getField()))
                .map(e -> "Revisa " + etiqueta(e.getField()) + ".")
                .distinct().collect(Collectors.joining("; "));
        String erroresGenerales = resultado.getGlobalErrors().stream()
                .map(e -> e.getDefaultMessage()).distinct().collect(Collectors.joining("; "));
        String errores = java.util.stream.Stream.of(erroresCampos, erroresGenerales)
                .filter(e -> !e.isBlank()).collect(Collectors.joining("; "));
        if (errores.isBlank()) return false;
        flash.addFlashAttribute("error", errores);
        flash.addFlashAttribute(nombreFormulario, resultado.getTarget());
        return true;
    }
}
