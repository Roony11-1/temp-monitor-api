package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CamaraLecturaResumenResponse
{
    private Instant timestamp;
    private Double promedio;
    private Double minimo;
    private Double maximo;
    private int conteo;
}