package io.github.roony11_1.temp_monitor.modules.camara.core.application.mapper;

import org.springframework.stereotype.Component;

import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.camara.response.CamaraSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;

@Component
public class CamaraMapper implements EntityMapper<Camara, CamaraSummaryResponse>
{
    @Override
    public CamaraSummaryResponse toSummaryResponse(Camara entity) 
    {
        return CamaraSummaryResponse.builder()
            .id(entity.getId())
            .nombre(entity.getNombre())
            .descripcion(entity.getDescripcion())
            .sucursalId(entity.getSucursal().getId())
            .sucursal(entity.getSucursal().getNombre())
            .temperaturaMin(entity.getTemperaturaMin())
            .temperaturaMax(entity.getTemperaturaMax())
            .estado(entity.isActivo())
            .eliminado(entity.getDeletedAt() != null)
            .build();
    }
}
