package com.phelti.frontend.vistas;

import com.phelti.frontend.Dtos.*;
import com.phelti.frontend.webservicesclients.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioViewController {
    private final UserApiClient usuarios;
    private final SkillApiClient habilidades;
    private final InterestApiClient intereses;
    private final MetricsApiClient metricas;
    private final RatingApiClient calificaciones;
    private final VistaSupport soporte;
    private final AvailabilityApiClient disponibilidad;

    public UsuarioViewController(UserApiClient usuarios,
            SkillApiClient habilidades, InterestApiClient intereses, MetricsApiClient metricas,
            RatingApiClient calificaciones, VistaSupport soporte, AvailabilityApiClient disponibilidad) {
        this.disponibilidad = disponibilidad;
        this.usuarios = usuarios;
        this.habilidades = habilidades;
        this.intereses = intereses;
        this.metricas = metricas;
        this.calificaciones = calificaciones;
        this.soporte = soporte;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", java.util.List.of());
        model.addAttribute("busquedaRealizada", false);
        model.addAttribute("habilidades", habilidades.listarCatalogo());
        model.addAttribute("intereses", intereses.listarCatalogo());
        return "usuarios/listado";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) Integer skillId,
                         @RequestParam(required = false) Integer interestId,
                         @RequestParam(required = false) String interactionMode,
                         @RequestParam(required = false) String dayOfWeek,
                         @RequestParam(required = false) String nombre, Model model) {
        String filtro = normalizarNombre(nombre);
        boolean buscar = !filtro.isBlank() || skillId != null || interestId != null
                || (interactionMode != null && !interactionMode.isBlank())
                || (dayOfWeek != null && !dayOfWeek.isBlank());
        model.addAttribute("busquedaRealizada", buscar);
        model.addAttribute("usuarios", buscar ? usuarios.buscar(skillId, interestId, interactionMode, dayOfWeek).stream()
                .filter(u -> normalizarNombre(u.getName()).contains(filtro)).toList() : java.util.List.of());
        model.addAttribute("nombre", nombre);
        model.addAttribute("habilidades", habilidades.listarCatalogo());
        model.addAttribute("intereses", intereses.listarCatalogo());
        model.addAttribute("skillId", skillId);
        model.addAttribute("interestId", interestId);
        model.addAttribute("interactionMode", interactionMode);
        model.addAttribute("dayOfWeek", dayOfWeek);
        return "usuarios/listado";
    }

    private String normalizarNombre(String nombre) {
        if (nombre == null) return "";
        return java.text.Normalizer.normalize(nombre.strip(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("\\s+", " ")
                .toLowerCase(java.util.Locale.ROOT);
    }

    @GetMapping("/registro")
    public String nuevo(Model model) {
        if (!model.containsAttribute("registroForm")) model.addAttribute("registroForm", new UserRequest());
        return "usuarios/registro";
    }

    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute("registroForm") UserRequest datos,
                           BindingResult resultado, HttpSession sesion, RedirectAttributes flash) {
        if (soporte.formularioInvalido(resultado, flash, "registroForm")) return "redirect:/usuarios/registro";
        UserResponse nuevo;
        try {
            nuevo = usuarios.registrar(datos);
        } catch (BackendApiException exception) {
            flash.addFlashAttribute("registroForm", datos);
            return soporte.error(flash, exception, "/usuarios/registro");
        }
        soporte.activar(sesion, nuevo);
        return soporte.correcto(flash, "Cuenta creada correctamente.", "/perfil");
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        model.addAttribute("usuario", usuarios.obtener(id));
        model.addAttribute("metricas", metricas.obtener(id));
        model.addAttribute("habilidadesUsuario", habilidades.deUsuario(id));
        model.addAttribute("interesesUsuario", intereses.deUsuario(id));
        model.addAttribute("calificaciones", calificaciones.recibidas(id));
        model.addAttribute("disponibilidades", disponibilidad.deUsuario(id));
        return "usuarios/detalle";
    }
}
