package io.github.roony11_1.temp_monitor.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la compactación (rollup + purga) de lecturas.
 * Ver {@code io.github.roony11_1.temp_monitor.modules.camara.core.application.CompactionService}.
 */
@Configuration
@Getter
public class CompactionConfig 
{
    @Value("${app.compactacion.enabled:false}")
    private boolean enabled;

    @Value("${app.compactacion.cron:0 0 3 * * SUN}")
    private String cron;

    @Value("${app.compactacion.retencion-dias:30}")
    private long retencionDias;

    @Value("${app.compactacion.retencion-meses:12}")
    private long retencionMeses;

    @Value("${app.compactacion.max-lote:100000}")
    private int maxLote;
}