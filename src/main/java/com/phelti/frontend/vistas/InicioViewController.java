package com.phelti.frontend.vistas;

import com.phelti.frontend.webservicesclients.PublicationApiClient;
import com.phelti.frontend.webservicesclients.UserApiClient;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioViewController {
    private final UserApiClient usuarios;
    private final PublicationApiClient publicaciones;
    private final VistaSupport soporte;

    public InicioViewController(UserApiClient usuarios, PublicationApiClient publicaciones,
                                VistaSupport soporte) {
        this.usuarios = usuarios;
        this.publicaciones = publicaciones;
        this.soporte = soporte;
    }

    @GetMapping("/")
    public String inicio(Model model, HttpSession sesion) {
        model.addAttribute("usuarios", usuarios.listar());
        model.addAttribute("publicaciones", publicaciones.buscar(null, null, null, null, null, null));
        soporte.nombres(model);
        model.addAttribute("seleccionado", soporte.usuarioActivo(sesion));
        return "inicio";
    }
}
