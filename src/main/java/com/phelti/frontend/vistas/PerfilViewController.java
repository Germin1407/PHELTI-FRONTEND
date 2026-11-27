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
@RequestMapping("/perfil")
public class PerfilViewController {
    private final UserApiClient usuarios;
    private final MetricsApiClient metricas;
    private final RatingApiClient calificaciones;
    private final SkillApiClient habilidades;
    private final InterestApiClient intereses;
    private final AvailabilityApiClient disponibilidad;
    private final VistaSupport soporte;

    public PerfilViewController(UserApiClient usuarios, MetricsApiClient metricas,
            RatingApiClient calificaciones, SkillApiClient habilidades,
            InterestApiClient intereses, AvailabilityApiClient disponibilidad, VistaSupport soporte) {
        this.usuarios = usuarios;
        this.metricas = metricas;
        this.calificaciones = calificaciones;
        this.habilidades = habilidades;
        this.intereses = intereses;
        this.disponibilidad = disponibilidad;
        this.soporte = soporte;
    }

    @GetMapping
    public String perfil(Model model, HttpSession sesion, RedirectAttributes flash) {
        Integer id = soporte.usuarioActivo(sesion);
        if (id == null) return soporte.iniciarSesion(flash);
        UserResponse usuario = usuarios.obtener(id);
        ProfileUpdateRequest formulario = new ProfileUpdateRequest();
        formulario.setName(usuario.getName());
        formulario.setDescription(usuario.getDescription());
        formulario.setLocation(usuario.getLocation());
        formulario.setPreferredInteractionMode(usuario.getPreferredInteractionMode());
        model.addAttribute("usuario", usuario);
        if (!model.containsAttribute("perfilForm")) model.addAttribute("perfilForm", formulario);
        if (!model.containsAttribute("disponibilidadForm")) {
            model.addAttribute("disponibilidadForm", new AvailabilityRequest());
        }
        model.addAttribute("metricas", metricas.obtener(id));
        model.addAttribute("calificaciones", calificaciones.recibidas(id));
        var propiasHabilidades = habilidades.deUsuario(id);
        var propiosIntereses = intereses.deUsuario(id);
        model.addAttribute("habilidadesCatalogo", habilidades.listarCatalogo().stream().filter(h -> propiasHabilidades.stream().noneMatch(p -> p.getSkillId().equals(h.getSkillId()))).toList());
        model.addAttribute("habilidadesUsuario", propiasHabilidades);
        model.addAttribute("interesesCatalogo", intereses.listarCatalogo().stream().filter(i -> propiosIntereses.stream().noneMatch(p -> p.getInterestId().equals(i.getInterestId()))).toList());
        model.addAttribute("interesesUsuario", propiosIntereses);
        model.addAttribute("disponibilidades", disponibilidad.deUsuario(id));
        return "perfil/perfil";
    }

    @PostMapping("/guardar")
    public String actualizar(@Valid @ModelAttribute("perfilForm") ProfileUpdateRequest datos,
            BindingResult resultado, HttpSession sesion, RedirectAttributes flash) {
        Integer id = soporte.usuarioActivo(sesion);
        if (id == null) return soporte.iniciarSesion(flash);
        if (soporte.formularioInvalido(resultado, flash, "perfilForm")) return "redirect:/perfil#datos";
        try {
            UserResponse actualizado = usuarios.actualizar(id, datos);
            soporte.activar(sesion, actualizado);
            return soporte.correcto(flash, "Perfil actualizado.", "/perfil#datos");
        } catch (BackendApiException exception) {
            flash.addFlashAttribute("perfilForm", datos);
            return soporte.error(flash, exception, "/perfil#datos");
        }
    }

    @PostMapping("/habilidades/asignar")
    public String asignarHabilidad(@RequestParam Integer skillId, HttpSession sesion, RedirectAttributes flash) {
        Integer id = soporte.usuarioActivo(sesion);
        if (id == null) return soporte.iniciarSesion(flash);
        try {
            habilidades.asignar(id, skillId);
            return soporte.correcto(flash, "Habilidad añadida.", "/perfil#habilidades");
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/perfil#habilidades");
        }
    }

    @PostMapping("/habilidades/{skillId}/quitar")
    public String quitarHabilidad(@PathVariable Integer skillId, HttpSession sesion, RedirectAttributes flash) {
        Integer id = soporte.usuarioActivo(sesion);
        if (id == null) return soporte.iniciarSesion(flash);
        try {
            habilidades.quitar(id, skillId);
            return soporte.correcto(flash, "Habilidad retirada.", "/perfil#habilidades");
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/perfil#habilidades");
        }
    }

    @PostMapping("/intereses/asignar")
    public String asignarInteres(@RequestParam Integer interestId, HttpSession sesion, RedirectAttributes flash) {
        Integer id = soporte.usuarioActivo(sesion);
        if (id == null) return soporte.iniciarSesion(flash);
        try {
            intereses.asignar(id, interestId);
            return soporte.correcto(flash, "Interés añadido.", "/perfil#intereses");
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/perfil#intereses");
        }
    }

    @PostMapping("/intereses/{interestId}/quitar")
    public String quitarInteres(@PathVariable Integer interestId, HttpSession sesion, RedirectAttributes flash) {
        Integer id = soporte.usuarioActivo(sesion);
        if (id == null) return soporte.iniciarSesion(flash);
        try {
            intereses.quitar(id, interestId);
            return soporte.correcto(flash, "Interés retirado.", "/perfil#intereses");
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/perfil#intereses");
        }
    }

    @PostMapping("/disponibilidad/agregar")
    public String agregarDisponibilidad(@Valid @ModelAttribute("disponibilidadForm") AvailabilityRequest datos,
            BindingResult resultado, HttpSession sesion, RedirectAttributes flash) {
        Integer id = soporte.usuarioActivo(sesion);
        if (id == null) return soporte.iniciarSesion(flash);
        if (soporte.formularioInvalido(resultado, flash, "disponibilidadForm")) return "redirect:/perfil#disponibilidad";
        try {
            disponibilidad.agregar(id, datos);
            return soporte.correcto(flash, "Disponibilidad registrada.", "/perfil#disponibilidad");
        } catch (BackendApiException exception) {
            flash.addFlashAttribute("disponibilidadForm", datos);
            return soporte.error(flash, exception, "/perfil#disponibilidad");
        }
    }

    @PostMapping("/disponibilidad/{id}/eliminar")
    public String eliminarDisponibilidad(@PathVariable Integer id, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        try {
            boolean propia = disponibilidad.deUsuario(activo).stream()
                    .anyMatch(franja -> id.equals(franja.getAvailabilityId()));
            if (!propia) return soporte.accesoDenegado(flash, "Esta franja no pertenece al usuario activo.", "/perfil#disponibilidad");
            disponibilidad.eliminar(id);
            return soporte.correcto(flash, "Disponibilidad eliminada.", "/perfil#disponibilidad");
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/perfil#disponibilidad");
        }
    }
}
