package com.phelti.frontend.vistas;

import com.phelti.frontend.Dtos.PublicationRequest;
import com.phelti.frontend.Dtos.PublicationResponse;
import com.phelti.frontend.webservicesclients.BackendApiException;
import com.phelti.frontend.webservicesclients.PublicationApiClient;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/publicaciones")
public class PublicacionViewController {
    private final PublicationApiClient publicaciones;
    private final VistaSupport soporte;
    private final com.phelti.frontend.webservicesclients.ProposalApiClient propuestas;
    private final com.phelti.frontend.webservicesclients.WalletApiClient billeteras;

    public PublicacionViewController(PublicationApiClient publicaciones, VistaSupport soporte, com.phelti.frontend.webservicesclients.ProposalApiClient propuestas, com.phelti.frontend.webservicesclients.WalletApiClient billeteras) {
        this.propuestas = propuestas;
        this.billeteras = billeteras;
        this.publicaciones = publicaciones;
        this.soporte = soporte;
    }

    private void saldo(Model model, Integer id) {
        try { model.addAttribute("saldoDisponible", billeteras.obtener(id).getAvailableBalance()); }
        catch (BackendApiException exception) { if (exception.getStatusCode() != 404) throw exception; model.addAttribute("saldoDisponible", java.math.BigDecimal.ZERO); }
    }

    @GetMapping
    public String muro(@RequestParam(required = false) String publicationType,
                       @RequestParam(required = false) String interactionMode,
                       @RequestParam(required = false) Integer categoryId,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) BigDecimal maxPheltAmount,
                       @RequestParam(required = false) String publicationStatus, Model model) {
        String texto = keyword == null ? "" : keyword.strip();
        boolean buscar = !texto.isBlank() || categoryId != null || maxPheltAmount != null
                || (publicationType != null && !publicationType.isBlank())
                || (interactionMode != null && !interactionMode.isBlank())
                || (publicationStatus != null && !publicationStatus.isBlank());
        model.addAttribute("busquedaRealizada", buscar);
        model.addAttribute("publicaciones", buscar ? publicaciones.buscar(publicationType, interactionMode,
                categoryId, texto, maxPheltAmount, publicationStatus) : List.of());
        model.addAttribute("soloMias", false);
        model.addAttribute("publicationType", publicationType);
        model.addAttribute("interactionMode", interactionMode);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("maxPheltAmount", maxPheltAmount);
        model.addAttribute("publicationStatus", publicationStatus);
        soporte.nombres(model);
        return "publicaciones/muro";
    }

    @GetMapping("/mias")
    public String propias(Model model, HttpSession sesion, RedirectAttributes flash) {
        Integer id = soporte.usuarioActivo(sesion);
        if (id == null) return soporte.iniciarSesion(flash);
        List<PublicationResponse> propias = publicaciones.listar().stream()
                .filter(pub -> id.equals(pub.getUserId())).toList();
        model.addAttribute("publicaciones", propias);
        model.addAttribute("soloMias", true);
        model.addAttribute("busquedaRealizada", true);
        soporte.nombres(model);
        return "publicaciones/muro";
    }

    @GetMapping("/nueva")
    public String nueva(Model model, HttpSession sesion, RedirectAttributes flash) {
        Integer id = soporte.usuarioActivo(sesion);
        if (id == null) return soporte.iniciarSesion(flash);
        if (!model.containsAttribute("publicacionForm")) {
            PublicationRequest datos = new PublicationRequest();
            datos.setUserId(id);
            datos.setPublicationStatus("PUBLISHED");
            model.addAttribute("publicacionForm", datos);
        }
        saldo(model, id);
        model.addAttribute("edicion", false);
        soporte.categorias(model);
        return "publicaciones/formulario";
    }

    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("publicacionForm") PublicationRequest datos,
                        BindingResult resultado, HttpSession sesion, RedirectAttributes flash) {
        Integer id = soporte.usuarioActivo(sesion);
        if (id == null) return soporte.iniciarSesion(flash);
        datos.setUserId(id);
        if (soporte.formularioInvalido(resultado, flash, "publicacionForm", "userId")) return "redirect:/publicaciones/nueva";
        try {
            PublicationResponse creada = publicaciones.crear(datos);
            return soporte.correcto(flash, "Publicación creada.", "/publicaciones/" + creada.getPublicationId());
        } catch (BackendApiException exception) {
            flash.addFlashAttribute("publicacionForm", datos);
            return soporte.error(flash, exception, "/publicaciones/nueva");
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model, HttpSession sesion) {
        var publicacion = publicaciones.obtener(id);
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo != null) {
            var relacionadas = propuestas.listar().stream().filter(p -> id.equals(p.getPublicationId())).toList();
            model.addAttribute("propuestaActiva", relacionadas.stream().filter(p -> activo.equals(p.getProposingUserId()) && ("PENDING".equals(p.getProposalStatus()) || "ACCEPTED".equals(p.getProposalStatus()))).findFirst().orElse(null));
            model.addAttribute("tienePendientes", relacionadas.stream().anyMatch(p -> "PENDING".equals(p.getProposalStatus())));
        }
        model.addAttribute("publicacion", publicacion);
        soporte.nombres(model);
        return "publicaciones/detalle";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        PublicationResponse original = publicaciones.obtener(id);
        if (!activo.equals(original.getUserId())) return soporte.accesoDenegado(flash,
                "Solo el autor puede editar esta publicación.", "/publicaciones/mias");

        if (!"PUBLISHED".equals(original.getPublicationStatus())) return soporte.accesoDenegado(flash, "Esta publicación no se puede editar en su estado actual.", "/publicaciones/" + id);
        PublicationRequest datos = new PublicationRequest();
        datos.setUserId(original.getUserId());
        datos.setCategoryId(original.getCategoryId());
        datos.setPublicationType(original.getPublicationType());
        datos.setTitle(original.getTitle());
        datos.setDescription(original.getDescription());
        datos.setInteractionMode(original.getInteractionMode());
        datos.setPheltAmount(original.getPheltAmount());
        datos.setNegotiable(original.isNegotiable());
        datos.setPublicationStatus(original.getPublicationStatus());
        datos.setDurationMinutes(original.getDurationMinutes());
        if (!model.containsAttribute("publicacionForm")) model.addAttribute("publicacionForm", datos);
        saldo(model, activo);
        model.addAttribute("edicion", true);
        model.addAttribute("publicacionId", id);
        soporte.categorias(model);
        return "publicaciones/formulario";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Integer id,
            @Valid @ModelAttribute("publicacionForm") PublicationRequest datos,
            BindingResult resultado, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        String editar = "/publicaciones/" + id + "/editar";
        try {
            PublicationResponse original = publicaciones.obtener(id);
            if (!activo.equals(original.getUserId())) {
                flash.addFlashAttribute("error", "No puedes modificar publicaciones de otros usuarios.");
                return "redirect:/publicaciones/mias";
            }
            datos.setUserId(original.getUserId());
            if (soporte.formularioInvalido(resultado, flash, "publicacionForm", "userId")) return "redirect:" + editar;
            publicaciones.actualizar(id, datos);
            return soporte.correcto(flash, "Publicación actualizada.", "/publicaciones/" + id);
        } catch (BackendApiException exception) {
            flash.addFlashAttribute("publicacionForm", datos);
            return soporte.error(flash, exception, editar);
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        try {
            PublicationResponse original = publicaciones.obtener(id);
            if (!activo.equals(original.getUserId())) {
                flash.addFlashAttribute("error", "No puedes eliminar publicaciones ajenas.");
                return "redirect:/publicaciones/mias";
            }
            publicaciones.eliminar(id);
            return soporte.correcto(flash, "Publicación retirada.", "/publicaciones/mias");
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/publicaciones/mias");
        }
    }
}
