package com.phelti.frontend.vistas;

import com.phelti.frontend.webservicesclients.BackendApiException;
import com.phelti.frontend.webservicesclients.UserApiClient;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sesion")
public class SesionViewController {
    private final UserApiClient usuarios;
    private final VistaSupport soporte;

    public SesionViewController(UserApiClient usuarios, VistaSupport soporte) {
        this.usuarios = usuarios;
        this.soporte = soporte;
    }

    @PostMapping("/seleccionar")
    public String seleccionar(@RequestParam Integer usuarioId, HttpSession sesion,
                             RedirectAttributes flash) {
        try {
            soporte.activar(sesion, usuarios.obtener(usuarioId));
            return soporte.correcto(flash, "Usuario activo seleccionado.", "/");
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/");
        }
    }

    @PostMapping("/salir")
    public String salir(HttpSession sesion, RedirectAttributes flash) {
        sesion.invalidate();
        return soporte.correcto(flash, "Selección de usuario finalizada.", "/");
    }
}
