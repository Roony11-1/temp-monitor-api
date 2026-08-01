package io.github.roony11_1.temp_monitor.modules.empresa.core.application.mapper;

import org.springframework.stereotype.Component;

import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.EmpresaSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;

@Component
public class EmpresaMapper implements EntityMapper<Empresa, EmpresaSummaryResponse>
{
    @Override
    public EmpresaSummaryResponse toSummaryResponse(Empresa entity) 
    {
        return EmpresaSummaryResponse.builder()
            .id(entity.getId())
            .nombre(entity.getNombre())
            .direccion(entity.getDireccion())
            .telefono(entity.getTelefono())
            .email(entity.getEmail())
            .activo(entity.isActivo())
            .build();
    }
}
