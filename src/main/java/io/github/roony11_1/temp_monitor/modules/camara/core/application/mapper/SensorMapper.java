package io.github.roony11_1.temp_monitor.modules.camara.core.application.mapper;

import org.springframework.stereotype.Component;

import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.SensorSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;

@Component
public class SensorMapper implements EntityMapper<Sensor, SensorSummaryResponse>
{
    @Override
    public SensorSummaryResponse toSummaryResponse(Sensor entity) 
    {
        return SensorSummaryResponse.builder()
            .id(entity.getId())
            .uuid(entity.getUuid())
            .macAddress(entity.getMacAddress())
            .camaraId(entity.getCamara() != null ? entity.getCamara().getId() : null)
            .camaraNombre(entity.getCamara() != null ? entity.getCamara().getNombre() : null)
            .sucursalId(entity.getCamara() != null ? entity.getCamara().getSucursal().getId() : null)
            .sucursalNombre(entity.getCamara() != null ? entity.getCamara().getSucursal().getNombre() : null)
            .empresaId(entity.getCamara() != null ? entity.getCamara().getSucursal().getEmpresa().getId() : null)
            .empresaNombre(entity.getCamara() != null ? entity.getCamara().getSucursal().getEmpresa().getNombre() : null)
            .estado(entity.getEstado())
            .build();
    }
}
