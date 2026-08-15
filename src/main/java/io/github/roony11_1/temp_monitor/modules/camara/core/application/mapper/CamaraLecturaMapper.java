package io.github.roony11_1.temp_monitor.modules.camara.core.application.mapper;

import org.springframework.stereotype.Component;

import io.github.roony11_1.temp_monitor.kernel.mapper.DetailEntityMapper;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraLecturaResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.CamaraLectura;

@Component
public class CamaraLecturaMapper implements DetailEntityMapper<CamaraLectura, CamaraLecturaResponse>
{
    @Override
    public CamaraLecturaResponse toResponse(CamaraLectura entity)
    {
        return CamaraLecturaResponse.builder()
            .timestamp(entity.getBucketStart())
            .muestreadoEn(entity.getMuestreadoEn())
            .promedio(entity.getPromedio())
            .sensores(entity.getConteoSensores())
            .build();
    }
}
