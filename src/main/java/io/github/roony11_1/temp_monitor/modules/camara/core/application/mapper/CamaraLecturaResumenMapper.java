package io.github.roony11_1.temp_monitor.modules.camara.core.application.mapper;

import org.springframework.stereotype.Component;

import io.github.roony11_1.temp_monitor.kernel.mapper.DetailEntityMapper;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraLecturaResumenResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.CamaraLecturaResumen;

@Component
public class CamaraLecturaResumenMapper implements DetailEntityMapper<CamaraLecturaResumen, CamaraLecturaResumenResponse>
{
    @Override
    public CamaraLecturaResumenResponse toResponse(CamaraLecturaResumen entity)
    {
        return CamaraLecturaResumenResponse.builder()
            .timestamp(entity.getBucketStart())
            .promedio(entity.getPromedio())
            .minimo(entity.getMinimo())
            .maximo(entity.getMaximo())
            .conteo(entity.getConteo())
            .build();
    }
}
