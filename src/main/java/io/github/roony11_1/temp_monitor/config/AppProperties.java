package io.github.roony11_1.temp_monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Propiedades centralizadas para umbrales y ventanas que antes estaban hardcodeadas
 * como {@code Duration.ofMinutes(15)} / {@code 5m} / {@code 24h} en Services.
 * OCP: cambiar un threshold es configuración, no código.
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Ventana ventana = new Ventana();
    private Dashboard dashboard = new Dashboard();

    @Data
    public static class Ventana {
        /** Ventana de agregación de temperatura de cámara (default 15m) */
        private Duration temperatura = Duration.ofMinutes(15);
        /** Ventana de muestreo / desde para CamaraLecturaService (default 15m) */
        private Duration muestra = Duration.ofMinutes(15);
    }

    @Data
    public static class Dashboard {
        /** Threshold para considerar sensor online (default 5m) */
        private Duration onlineThreshold = Duration.ofMinutes(5);
        /** Ventana de historial de temperatura 24h (default 24h) */
        private Duration temperaturaVentana = Duration.ofHours(24);
    }
}
