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
@RequestMapping("/actividades")
public class ActividadViewController {
    private final ActivityApiClient actividades;
    private final ProposalApiClient propuestas;
    private final PublicationApiClient publicaciones;
    private final RatingApiClient calificaciones;
    private final VistaSupport soporte;

    public ActividadViewController(ActivityApiClient actividades, ProposalApiClient propuestas,
            PublicationApiClient publicaciones, RatingApiClient calificaciones, VistaSupport soporte) {
        this.actividades = actividades;
        this.propuestas = propuestas;
        this.publicaciones = publicaciones;
        this.calificaciones = calificaciones;
        this.soporte = soporte;
    }

    @GetMapping
    public String gestion(Model model, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        model.addAttribute("actividades", actividades.deUsuario(activo).stream().sorted(java.util.Comparator.comparing(ActivityResponse::getActivityDate).thenComparing(ActivityResponse::getActivityTime).reversed()).toList());
        soporte.nombres(model);
        return "actividades/gestion";
    }

    @GetMapping("/programar")
    public String programar(@RequestParam Integer propuestaId, Model model,
            HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        ProposalResponse propuesta = propuestas.obtener(propuestaId);
        PublicationResponse publicacion = publicaciones.obtener(propuesta.getPublicationId());
        if (!participaEnPropuesta(activo, propuesta, publicacion)) {
            flash.addFlashAttribute("error", "No participas en esta propuesta.");
            return "redirect:/propuestas";
        }
        if (!"ACCEPTED".equals(propuesta.getProposalStatus())) {
            flash.addFlashAttribute("error", "Solo se pueden programar propuestas aceptadas.");
            return "redirect:/propuestas";
        }
        var existente = actividades.deUsuario(activo).stream().filter(a -> propuestaId.equals(a.getProposalId())).findFirst();
        if (existente.isPresent()) return "redirect:/actividades/" + existente.get().getActivityId();
        ActivityRequest datos = new ActivityRequest();
        datos.setProposalId(propuestaId);
        datos.setDurationMinutes(publicacion.getDurationMinutes());
        if (!model.containsAttribute("actividadForm")) model.addAttribute("actividadForm", datos);
        var horarios = actividades.horarios(propuestaId);
        Integer solicitante = "OFFERS_SUPPORT".equals(publicacion.getPublicationType()) ? propuesta.getProposingUserId() : publicacion.getUserId();
        model.addAttribute("solicitanteActivo", activo.equals(solicitante));
        model.addAttribute("horarios", horarios);
        model.addAttribute("horarioPendiente", horarios.stream().filter(h -> "PENDING".equals(h.getStatus())).findFirst().orElse(null));
        model.addAttribute("propuesta", propuesta);
        model.addAttribute("publicacion", publicacion);
        soporte.nombres(model);
        return "actividades/programar";
    }

    @PostMapping("/programar")
    public String crear(@Valid @ModelAttribute("actividadForm") ActivityRequest datos,
            BindingResult resultado, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        String regresar = datos.getProposalId() == null ? "/propuestas" :
                "/actividades/programar?propuestaId=" + datos.getProposalId();
        if (soporte.formularioInvalido(resultado, flash, "actividadForm")) return "redirect:" + regresar;
        try {
            ProposalResponse propuesta = propuestas.obtener(datos.getProposalId());
            PublicationResponse pub = publicaciones.obtener(propuesta.getPublicationId());
            if (!participaEnPropuesta(activo, propuesta, pub)) {
                flash.addFlashAttribute("error", "No puedes programar una propuesta ajena.");
                return "redirect:/propuestas";
            }
            var existente = actividades.deUsuario(activo).stream().filter(a -> datos.getProposalId().equals(a.getProposalId())).findFirst();
            if (existente.isPresent()) return "redirect:/actividades/" + existente.get().getActivityId();
            actividades.proponerHorario(datos);
            return soporte.correcto(flash, "Horario propuesto. El prestador debe aceptarlo antes de reservar los phelts.", regresar);
        } catch (BackendApiException exception) {
            flash.addFlashAttribute("actividadForm", datos);
            return soporte.error(flash, exception, regresar);
        }
    }

    @PostMapping("/horario/{id}/aceptar")
    public String aceptarHorario(@PathVariable Integer id, @RequestParam Integer propuestaId, HttpSession sesion, RedirectAttributes flash) {
        if (soporte.usuarioActivo(sesion) == null) return soporte.iniciarSesion(flash);
        try {
            ActivityResponse actividad = actividades.aceptarHorario(id);
            return soporte.correcto(flash, "Horario acordado. Actividad programada y phelts reservados.", "/actividades/" + actividad.getActivityId());
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/actividades/programar?propuestaId=" + propuestaId);
        }
    }

    @PostMapping("/horario/{id}/rechazar")
    public String rechazarHorario(@PathVariable Integer id, @RequestParam Integer propuestaId, HttpSession sesion, RedirectAttributes flash) {
        if (soporte.usuarioActivo(sesion) == null) return soporte.iniciarSesion(flash);
        try {
            actividades.rechazarHorario(id);
            return soporte.correcto(flash, "Solicitud de horario cerrada. El solicitante puede proponer otro horario.", "/actividades/programar?propuestaId=" + propuestaId);
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/actividades/programar?propuestaId=" + propuestaId);
        }
    }

    @PostMapping("/acuerdo/{id}/cancelar")
    public String cancelarAcuerdo(@PathVariable Integer id, HttpSession sesion, RedirectAttributes flash) {
        if (soporte.usuarioActivo(sesion) == null) return soporte.iniciarSesion(flash);
        try {
            actividades.cancelarAcuerdo(id);
            return soporte.correcto(flash, "Acuerdo cancelado. No se reservaron phelts.", "/propuestas");
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/actividades/programar?propuestaId=" + id);
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, HttpSession sesion,
                          Model model, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        ActivityResponse actividad = actividades.obtener(id);
        if (!esParticipante(activo, actividad)) {
            flash.addFlashAttribute("error", "No participas en esta actividad.");
            return "redirect:/actividades";
        }
        model.addAttribute("actividad", actividad);
        var confirmaciones = actividades.confirmaciones(id);
        var ratings = calificaciones.deActividad(id);
        boolean confirmado = confirmaciones.stream().anyMatch(c -> activo.equals(c.getUserId()));
        boolean calificado = ratings.stream().anyMatch(r -> activo.equals(r.getRaterId()));
        boolean termino = !java.time.LocalDateTime.now(java.time.ZoneId.of("America/Bogota")).isBefore(java.time.LocalDateTime.of(actividad.getActivityDate(), actividad.getActivityTime()).plusMinutes(actividad.getDurationMinutes()));
        model.addAttribute("confirmaciones", confirmaciones);
        model.addAttribute("calificaciones", ratings);
        model.addAttribute("puedeConfirmar", "SCHEDULED".equals(actividad.getActivityStatus()) && termino && !confirmado);
        model.addAttribute("yaConfirmo", confirmado);
        model.addAttribute("puedeCalificar", "COMPLETED".equals(actividad.getActivityStatus()) && !calificado);
        model.addAttribute("yaCalifico", calificado);
        model.addAttribute("publicacion", publicaciones.obtener(actividad.getPublicationId()));
        if (!model.containsAttribute("calificacionForm")) {
            RatingRequest r = new RatingRequest();
            r.setActivityId(id);
            r.setRaterId(activo);
            model.addAttribute("calificacionForm", r);
        }
        soporte.nombres(model);
        return "actividades/detalle";
    }

    @PostMapping("/{id}/confirmar")
    public String confirmar(@PathVariable Integer id, @RequestParam String checkInCode,
                           HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        try {
            ActivityResponse actividad = actividades.obtener(id);
            if (!esParticipante(activo, actividad)) {
                flash.addFlashAttribute("error", "No puedes confirmar actividades ajenas.");
                return "redirect:/actividades";
            }
            ConfirmationRequest datos = new ConfirmationRequest();
            datos.setUserId(activo);
            datos.setCheckInCode(checkInCode);
            actividades.confirmar(id, datos);
            return soporte.correcto(flash, "Confirmación registrada.", "/actividades/" + id);
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/actividades/" + id);
        }
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Integer id, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        try {
            ActivityResponse actividad = actividades.obtener(id);
            if (!esParticipante(activo, actividad)) {
                flash.addFlashAttribute("error", "No puedes cancelar actividades ajenas.");
                return "redirect:/actividades";
            }
            actividades.cancelar(id);
            return soporte.correcto(flash, "Actividad cancelada; reserva liberada.", "/actividades/" + id);
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/actividades/" + id);
        }
    }

    @PostMapping("/{id}/calificar")
    public String calificar(@PathVariable Integer id,
            @Valid @ModelAttribute("calificacionForm") RatingRequest datos,
            BindingResult resultado, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        datos.setActivityId(id);
        datos.setRaterId(activo);
        if (soporte.formularioInvalido(resultado, flash, "calificacionForm", "activityId", "raterId")) {
            return "redirect:/actividades/" + id;
        }
        try {
            ActivityResponse actividad = actividades.obtener(id);
            if (!esParticipante(activo, actividad)) {
                flash.addFlashAttribute("error", "No puedes calificar actividades ajenas.");
                return "redirect:/actividades";
            }
            calificaciones.crear(datos);
            return soporte.correcto(flash, "Calificación registrada.", "/actividades/" + id);
        } catch (BackendApiException exception) {
            flash.addFlashAttribute("calificacionForm", datos);
            return soporte.error(flash, exception, "/actividades/" + id);
        }
    }

    private boolean participaEnPropuesta(Integer id, ProposalResponse propuesta, PublicationResponse pub) {
        return id.equals(propuesta.getProposingUserId()) || id.equals(pub.getUserId());
    }

    private boolean esParticipante(Integer id, ActivityResponse actividad) {
        return id.equals(actividad.getRequesterId()) || id.equals(actividad.getParticipantId());
    }
}
