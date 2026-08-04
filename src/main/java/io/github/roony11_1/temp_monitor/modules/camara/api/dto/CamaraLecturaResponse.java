package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.CamaraLectura;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CamaraLecturaResponse 
{
    private Instant timestamp;
    private Instant muestreadoEn;
    private Double promedio;
    private int sensores;

    public static CamaraLecturaResponse from(CamaraLectura lectura)
    {
        return CamaraLecturaResponse.builder()
            .timestamp(lectura.getBucketStart())
            .muestreadoEn(lectura.getMuestreadoEn())
            .promedio(lectura.getPromedio())
            .sensores(lectura.getConteoSensores())
            .build();
    }
}