package com.phelti.frontend.vistas;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component("formatoVista")
public class FormatoVista {
    private static final ZoneId ZONE = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.forLanguageTag("es-CO"));
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    public String fecha(Object value) {
        if (value == null) return "Sin fecha";
        if (value instanceof Instant i) value = i.atZone(ZONE).toLocalDateTime();
        if (value instanceof LocalDateTime d) return DATE.format(d) + " · " + TIME.format(d);
        if (value instanceof LocalDate d) return DATE.format(d);
        if (value instanceof LocalTime h) return TIME.format(h);
        return value.toString();
    }
    public String razon(String valor) {
        if (valor == null) return "Coincidencia con tu perfil";
        if (valor.startsWith("you can help with: ")) return "Puedes ayudar con: " + valor.substring(19);
        if (valor.startsWith("matches your interest: ")) return "Coincide con tu interés: " + valor.substring(23);
        if (valor.startsWith("matches your preferred mode: ")) return "Coincide con tu modalidad preferida: " + (valor.endsWith("VIRTUAL") ? "Virtual" : "Presencial");
        if (valor.startsWith("both have availability on ")) {
            try { return "Disponibilidad compartida: " + DayOfWeek.valueOf(valor.substring(26)).getDisplayName(java.time.format.TextStyle.FULL, Locale.forLanguageTag("es-CO")); }
            catch (IllegalArgumentException ignored) { return "Disponibilidad compartida"; }
        }
        return "Coincidencia con tu perfil";
    }
    public String importe(java.math.BigDecimal amount) {
        if (amount == null) return "—";
        var format = java.text.NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO"));
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return format.format(amount);
    }
    public String movimiento(String tipo) {
        return switch (tipo) {
            case "DEPOSIT" -> "Depósito en la billetera";
            case "RESERVE" -> "Reserva para una actividad";
            case "RELEASE" -> "Liberación al saldo disponible";
            case "TRANSFER_OUT" -> "Pago por una actividad completada";
            case "TRANSFER_IN" -> "Recepción por una actividad completada";
            default -> "Movimiento de billetera";
        };
    }
}
