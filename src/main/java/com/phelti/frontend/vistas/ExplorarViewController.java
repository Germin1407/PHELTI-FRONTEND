package com.phelti.frontend.vistas;

import com.phelti.frontend.webservicesclients.MatchingApiClient;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/explorar")
public class ExplorarViewController {
    private final MatchingApiClient matching;
    private final VistaSupport soporte;

    public ExplorarViewController(MatchingApiClient matching, VistaSupport soporte) {
        this.matching = matching;
        this.soporte = soporte;
    }

    @GetMapping
    public String explorar() { return "redirect:/publicaciones"; }

    @GetMapping("/recomendadas")
    public String recomendadas(Model model, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        model.addAttribute("coincidencias", matching.recomendar(activo));
        soporte.nombres(model);
        return "explorar/recomendadas";
    }
}
