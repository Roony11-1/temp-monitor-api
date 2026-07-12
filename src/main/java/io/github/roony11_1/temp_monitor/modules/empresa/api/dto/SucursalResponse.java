package io.github.roony11_1.temp_monitor.modules.empresa.api.dto;

import java.time.Instant;

import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SucursalResponse 
{
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private Long empresaId;
    private boolean activo;
    private Instant createdAt;
    private Instant updatedAt;

    public static SucursalResponse toResponse(Sucursal sucursal) 
    {
        return SucursalResponse.builder()
            .id(sucursal.getId())
            .nombre(sucursal.getNombre())
            .direccion(sucursal.getDireccion())
            .telefono(sucursal.getTelefono())
            .empresaId(sucursal.getEmpresa().getId())
            .activo(sucursal.isActivo())
            .createdAt(sucursal.getCreatedAt())
            .updatedAt(sucursal.getUpdatedAt())
            .build();
    }
}
