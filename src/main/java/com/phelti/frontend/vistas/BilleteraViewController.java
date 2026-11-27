package com.phelti.frontend.vistas;

import com.phelti.frontend.Dtos.DepositRequest;
import com.phelti.frontend.Dtos.WalletResponse;
import com.phelti.frontend.webservicesclients.BackendApiException;
import com.phelti.frontend.webservicesclients.WalletApiClient;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/billetera")
public class BilleteraViewController {
    private final WalletApiClient billeteras;
    private final VistaSupport soporte;

    public BilleteraViewController(WalletApiClient billeteras, VistaSupport soporte) {
        this.billeteras = billeteras;
        this.soporte = soporte;
    }

    @GetMapping
    public String resumen(Model model, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        WalletResponse wallet = null;
        try {
            wallet = billeteras.obtener(activo);
            model.addAttribute("movimientos", billeteras.movimientos(activo).stream().sorted(java.util.Comparator.comparing(com.phelti.frontend.Dtos.TransactionResponse::getCreatedAt, java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())).thenComparing(com.phelti.frontend.Dtos.TransactionResponse::getTransactionId, java.util.Comparator.reverseOrder())).toList());
        } catch (BackendApiException exception) {
            if (exception.getStatusCode() != 404) throw exception;

            model.addAttribute("movimientos", List.of());
        }
        model.addAttribute("billetera", wallet);
        model.addAttribute("tieneBilletera", wallet != null);
        if (!model.containsAttribute("depositoForm")) model.addAttribute("depositoForm", new DepositRequest());
        soporte.usuarios(model);
        return "billetera/resumen";
    }

    @PostMapping("/crear")
    public String crear(HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        try {
            billeteras.crear(activo);
            return soporte.correcto(flash, "Billetera creada.", "/billetera");
        } catch (BackendApiException exception) {
            return soporte.error(flash, exception, "/billetera");
        }
    }

    @PostMapping("/recargar")
    public String recargar(@Valid @ModelAttribute("depositoForm") DepositRequest datos,
            BindingResult resultado, HttpSession sesion, RedirectAttributes flash) {
        Integer activo = soporte.usuarioActivo(sesion);
        if (activo == null) return soporte.iniciarSesion(flash);
        if (soporte.formularioInvalido(resultado, flash, "depositoForm")) return "redirect:/billetera";
        try {
            billeteras.depositar(activo, datos);
            return soporte.correcto(flash, "Depósito de phelts registrado.", "/billetera");
        } catch (BackendApiException exception) {
            flash.addFlashAttribute("depositoForm", datos);
            return soporte.error(flash, exception, "/billetera");
        }
    }
}
