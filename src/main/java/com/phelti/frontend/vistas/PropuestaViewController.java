package com.phelti.frontend.vistas;

import com.phelti.frontend.Dtos.ProposalRequest;
import com.phelti.frontend.Dtos.ProposalResponse;
import com.phelti.frontend.Dtos.PublicationResponse;
import com.phelti.frontend.webservicesclients.BackendApiException;
import com.phelti.frontend.webservicesclients.ProposalApiClient;
import com.phelti.frontend.webservicesclients.PublicationApiClient;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/propuestas")
public class PropuestaViewController {
    private final ProposalApiClient propuestas;
    private final PublicationApiClient publicaciones;
    private final VistaSupport soporte;
    private final com.phelti.frontend.webservicesclients.WalletApiClient billeteras;
    private final com.phelti.frontend.webservicesclients.ActivityApiClient actividades;

    public PropuestaViewController(ProposalApiClient propuestas, PublicationApiClient publicaciones,
                                   VistaSupport soporte, com.phelti.frontend.webservicesclients.ActivityApiClient actividades, com.phelti.frontend.webservicesclients.WalletApiClient billeteras) {
        this.billeteras = billeteras;
        this.actividades = actividades;
        this.propuestas = propuestas;
        this.publicaciones = publicaciones;
        this.soporte = soporte;
    }

    @GetMapping
    public String gestion(Model model, HttpSession sesion, RedirectAttributes flash) {
        Integer id = soporte.usuarioActivo(sesion);
        if (id == null) return soporte.iniciarSesion(flash);
        Map<Integer, PublicationResponse> publicacionesMap = publicaciones.listar().stream()
                .collect(Collectors.toMap(PublicationResponse::getPublicationId, Function.identity()));
        List<ProposalResponse> todas = propuestas.listar().stream().sorted(java.util.Comparator.comparing(ProposalResponse::getCreatedAt, java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder()))).toList();
        model.addAttribute("propuestasEnviadas", todas.stream()
                .filter(p -> id.equals(p.getProposingUserId())).toList());
        model.addAttribute("propuestasRecibidas", todas.stream()
                .filter(p -> {
                    PublicationResponse pub = publicacionesMap.get(p.getPublicationId());
                    return pub != null && id.equals(pub.getUserId());
                }).toList());
        model.addAttribute("publicacionesMap", publicacionesMap);
        model.addAttribute("actividadesMap", actividades.deUsuario(id).stream().collect(Collectors.toMap(com.phelti.frontend.Dtos.ActivityResponse::getProposalId, Function.identity(), (a, c) -> a)));
        soporte.nombres(model);
        return "propuestas/gestion";
    }

    @GetMapping("/nueva")
    public String nueva(@RequestParam Integer publicacionId, Model model,
                        HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        PublicationResponse pub = publicaciones.obtener(publicacionId);
        if (activo.equals(pub.getUserId())) {
            flash.addFlashAttribute("error", "No puedes proponer en tu propia publicación.");
            return "redirect:/publicaciones/" + publicacionId;
        }
        if (!"PUBLISHED".equals(pub.getPublicationStatus())) {
            flash.addFlashAttribute("error", "Esta publicación ya no recibe propuestas.");
            return "redirect:/publicaciones/" + publicacionId;
        }
        var existente = propuestas.listar().stream().filter(p -> publicacionId.equals(p.getPublicationId()) && activo.equals(p.getProposingUserId()) && ("PENDING".equals(p.getProposalStatus()) || "ACCEPTED".equals(p.getProposalStatus()))).findFirst();
        if (existente.isPresent()) return "redirect:/propuestas/" + existente.get().getProposalId();
        ProposalRequest datos = new ProposalRequest();
        datos.setPublicationId(publicacionId);
        datos.setProposingUserId(activo);
        if (!model.containsAttribute("propuestaForm")) model.addAttribute("propuestaForm", datos);
        model.addAttribute("publicacion", pub);
        if ("OFFERS_SUPPORT".equals(pub.getPublicationType())) {
            try { model.addAttribute("saldoDisponible", billeteras.obtener(activo).getAvailableBalance()); }
            catch (BackendApiException exception) { if (exception.getStatusCode() != 404) throw exception; model.addAttribute("saldoDisponible", java.math.BigDecimal.ZERO); }
        }
        soporte.nombres(model);
        return "propuestas/formulario";
    }

    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("propuestaForm") ProposalRequest datos,
                        BindingResult resultado, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        datos.setProposingUserId(activo);
        String regresar = datos.getPublicationId() == null ? "/publicaciones" :
                "/propuestas/nueva?publicacionId=" + datos.getPublicationId();
        if (soporte.formularioInvalido(resultado, flash, "propuestaForm", "proposingUserId")) return "redirect:" + regresar;
        try {
            PublicationResponse pub = publicaciones.obtener(datos.getPublicationId());
            if (activo.equals(pub.getUserId())) {
                flash.addFlashAttribute("error", "No puedes proponer en una publicación propia.");
                return "redirect:/publicaciones/" + pub.getPublicationId();
            }
            propuestas.crear(datos);
            return soporte.correcto(flash, "Propuesta enviada.", "/propuestas");
        } catch (BackendApiException exception) {
            flash.addFlashAttribute("propuestaForm", datos);
            return soporte.error(flash, exception, regresar);
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model,
                          HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        ProposalResponse propuesta = propuestas.obtener(id);
        PublicationResponse publicacion = publicaciones.obtener(propuesta.getPublicationId());
        if (!activo.equals(propuesta.getProposingUserId()) && !activo.equals(publicacion.getUserId())) {
            flash.addFlashAttribute("error", "La propuesta no corresponde al usuario activo.");
            return "redirect:/propuestas";
        }
        model.addAttribute("actividadAsociada", actividades.deUsuario(activo).stream().filter(a -> id.equals(a.getProposalId())).findFirst().orElse(null));
        model.addAttribute("propuesta", propuesta);
        model.addAttribute("publicacion", publicacion);
        soporte.nombres(model);
        return "propuestas/detalle";
    }

    @PostMapping("/{id}/aceptar")
    public String aceptar(@PathVariable Integer id, HttpSession sesion, RedirectAttributes flash) {
        return cambiarEstado(id, true, sesion, flash);
    }

    @PostMapping("/{id}/rechazar")
    public String rechazar(@PathVariable Integer id, HttpSession sesion, RedirectAttributes flash) {
        return cambiarEstado(id, false, sesion, flash);
    }

    private String cambiarEstado(Integer id, boolean aceptar, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        try {
            ProposalResponse p = propuestas.obtener(id);
            PublicationResponse pub = publicaciones.obtener(p.getPublicationId());
            if (!activo.equals(pub.getUserId())) {
                flash.addFlashAttribute("error", "Solo el dueño de la publicación puede decidir sobre propuestas.");
                return "redirect:/propuestas";
            }
            if (aceptar) propuestas.aceptar(id); else propuestas.rechazar(id);
            return soporte.correcto(flash, aceptar ? "Propuesta aceptada." : "Propuesta rechazada.", "/propuestas");
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/propuestas");
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        try {
            ProposalResponse propuesta = propuestas.obtener(id);
            if (!activo.equals(propuesta.getProposingUserId())) {
                flash.addFlashAttribute("error", "Solo el proponente puede eliminar su propuesta.");
                return "redirect:/propuestas";
            }
            propuestas.eliminar(id);
            return soporte.correcto(flash, "Propuesta retirada.", "/propuestas");
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/propuestas");
        }
    }
}
