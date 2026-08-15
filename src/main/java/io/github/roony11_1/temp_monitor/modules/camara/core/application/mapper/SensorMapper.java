package io.github.roony11_1.temp_monitor.modules.camara.core.application.mapper;

import org.springframework.stereotype.Component;

import io.github.roony11_1.temp_monitor.kernel.mapper.DetailEntityMapper;
import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.SensorResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.SensorSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;

@Component
public class SensorMapper implements EntityMapper<Sensor, SensorSummaryResponse>, DetailEntityMapper<Sensor, SensorResponse>
{
    private final DetailEntityMapper<Camara, CamaraResponse> camaraMapper;

    public SensorMapper(DetailEntityMapper<Camara, CamaraResponse> camaraMapper)
    {
        this.camaraMapper = camaraMapper;
    }

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
            .eliminado(entity.getDeletedAt() != null)
            .build();
    }

    @Override
    public SensorResponse toResponse(Sensor entity) 
    {
        SensorResponse response = new SensorResponse();
        response.setId(entity.getId());
        response.setUuid(entity.getUuid());
        response.setApiKeyHash(entity.getApiKeyHash());
        response.setMacAddress(entity.getMacAddress());

        if (entity.getCamara() != null)
        {
            response.setCamara(camaraMapper.toResponse(entity.getCamara()));
            response.setSucursalId(entity.getCamara().getSucursal().getId());
            response.setSucursalNombre(entity.getCamara().getSucursal().getNombre());
            response.setEmpresaId(entity.getCamara().getSucursal().getEmpresa().getId());
            response.setEmpresaNombre(entity.getCamara().getSucursal().getEmpresa().getNombre());
        }

        response.setUltimoContacto(entity.getUltimoContacto());
        response.setEstado(entity.getEstado());
        response.setEliminado(entity.getDeletedAt() != null);
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        return response;
    }
}
