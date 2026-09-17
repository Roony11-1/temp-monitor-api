package io.github.roony11_1.temp_monitor.kernel.domain;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.RangoTemperaturaInvalidoException;

/**
 * Value Object OCP para rango de temperatura.
 * Centraliza la validación que estaba duplicada en {@code CamaraService.validarRango}.
 * Futuros rangos (ej. humedad) pueden extender sin modificar callers.
 */
public record RangoTemperatura(Double min, Double max) {

    public RangoTemperatura {
        if (min != null && max != null && min >= max) {
            throw new RangoTemperaturaInvalidoException();
        }
        // OCP: agregar validaciones adicionales (ej. min > -50, max < 100) aquí, sin tocar Services.
    }

    public static void validar(Double min, Double max) {
        new RangoTemperatura(min, max);
    }
}
