package io.github.roony11_1.temp_monitor.modules.empresa.core.application.mapper;

import org.springframework.stereotype.Component;

import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.SucursalSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;

@Component
public class SucursalMapper implements EntityMapper<Sucursal, SucursalSummaryResponse>
{
    @Override
    public SucursalSummaryResponse toSummaryResponse(Sucursal entity) 
    {
        return SucursalSummaryResponse.builder()
            .id(entity.getId())
            .nombre(entity.getNombre())
            .direccion(entity.getDireccion())
            .telefono(entity.getTelefono())
            .empresa(entity.getEmpresa().getNombre())
            .empresaId(entity.getEmpresa().getId())
            .activo(entity.isActivo())
            .eliminado(entity.getDeletedAt() != null)
            .build();
    }
}
