package io.github.roony11_1.temp_monitor.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class CamaraMuestreoConfig 
{
    @Value("${app.camara-muestreo.cadencia-ms:165000}")
    private long cadenciaMs;

    @Value("${app.camara-muestreo.initial-delay-ms:60000}")
    private long initialDelayMs;

    public long getCadenciaSegundos()
    {
        return cadenciaMs / 1000;
    }
}