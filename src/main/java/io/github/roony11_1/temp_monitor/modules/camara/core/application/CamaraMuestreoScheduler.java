package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CamaraMuestreoScheduler 
{
    private final CamaraLecturaService camaraLecturaService;

    @Scheduled(fixedDelay = 330000, initialDelay = 330000)
    public void muestrear()
    {
        camaraLecturaService.muestrear();
    }
}