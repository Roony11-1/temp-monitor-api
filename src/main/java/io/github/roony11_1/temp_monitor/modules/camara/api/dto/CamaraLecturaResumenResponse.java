package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.CamaraLecturaResumen;
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

    public static CamaraLecturaResumenResponse from(CamaraLecturaResumen resumen)
    {
        return CamaraLecturaResumenResponse.builder()
            .timestamp(resumen.getBucketStart())
            .promedio(resumen.getPromedio())
            .minimo(resumen.getMinimo())
            .maximo(resumen.getMaximo())
            .conteo(resumen.getConteo())
            .build();
    }
}