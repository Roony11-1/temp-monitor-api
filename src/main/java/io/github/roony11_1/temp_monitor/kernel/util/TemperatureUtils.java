package io.github.roony11_1.temp_monitor.kernel.util;

import java.time.Duration;

/**
 * Utilidades OCP para manejo de temperatura.
 * Centraliza el redondeo a 1 decimal y constantes de ventana temporal,
 * evitando duplicación de {@code Math.round(x*10)/10} y {@code Duration.ofMinutes(15)}
 * dispersos en {@code CamaraService}, {@code CamaraLecturaService}, {@code DashboardService}.
 */
public final class TemperatureUtils {

    private TemperatureUtils() {}

    public static final Duration VENTANA_15_MIN = Duration.ofMinutes(15);
    public static final Duration ONLINE_THRESHOLD_5_MIN = Duration.ofMinutes(5);
    public static final Duration VENTANA_24_H = Duration.ofHours(24);

    /**
     * Redondea a un decimal (ej. 23.46 -> 23.5). Null-safe.
     */
    public static Double round1(Double value) {
        if (value == null) return null;
        return Math.round(value * 10.0) / 10.0;
    }

    public static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
