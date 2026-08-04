package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CamaraMuestreoScheduler 
{
    private final CamaraLecturaService camaraLecturaService;

    @Scheduled(fixedDelayString = "${app.camara-muestreo.cadencia-ms:165000}", initialDelayString = "${app.camara-muestreo.initial-delay-ms:60000}")
    public void muestrear()
    {
        camaraLecturaService.muestrear();
    }
}