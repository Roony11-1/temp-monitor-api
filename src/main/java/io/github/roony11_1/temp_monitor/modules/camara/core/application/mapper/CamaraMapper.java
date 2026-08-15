package io.github.roony11_1.temp_monitor.modules.camara.core.application.mapper;

import org.springframework.stereotype.Component;

import io.github.roony11_1.temp_monitor.kernel.mapper.DetailEntityMapper;
import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.camara.response.CamaraSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;

@Component
public class CamaraMapper implements EntityMapper<Camara, CamaraSummaryResponse>, DetailEntityMapper<Camara, CamaraResponse>
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

    @Override
    public CamaraResponse toResponse(Camara entity) 
    {
        CamaraResponse response = new CamaraResponse();
        response.setId(entity.getId());
        response.setNombre(entity.getNombre());
        response.setDescripcion(entity.getDescripcion());
        response.setSucursalId(entity.getSucursal().getId());
        response.setTemperaturaMin(entity.getTemperaturaMin());
        response.setTemperaturaMax(entity.getTemperaturaMax());
        response.setActivo(entity.isActivo());
        response.setEliminado(entity.getDeletedAt() != null);
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
