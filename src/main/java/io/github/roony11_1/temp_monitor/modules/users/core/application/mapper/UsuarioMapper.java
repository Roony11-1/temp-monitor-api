package io.github.roony11_1.temp_monitor.modules.users.core.application.mapper;

import org.springframework.stereotype.Component;

import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;

@Component
public class UsuarioMapper implements EntityMapper<Usuario, UsuarioSummaryResponse>
{
    @Override
    public UsuarioSummaryResponse toSummaryResponse(Usuario entity) 
    {
        return UsuarioSummaryResponse.builder()
            .id(entity.getId())
            .email(entity.getEmail())
            .nombre(entity.getNombre())
            .telefono(entity.getTelefono())
            .empresa(entity.getEmpresa() != null ? entity.getEmpresa().getNombre() : null)
            .empresaId(entity.getEmpresaId())
            .sucursal(entity.getSucursal() != null ? entity.getSucursal().getNombre() : null)
            .sucursalId(entity.getSucursalId())
            .roles(entity.getRoles().stream().map(Enum::name).toList())
            .activo(entity.isActivo())
            .eliminado(entity.getDeletedAt() != null)
            .build();
    }
}
