package io.github.roony11_1.temp_monitor.modules.empresa.api.dto;

import java.time.Instant;

import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpresaResponse 
{
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private boolean activo;
    private Instant createdAt;
    private Instant updatedAt;

    public static EmpresaResponse toResponse(Empresa empresa) 
    {
        return EmpresaResponse.builder()
            .id(empresa.getId())
            .nombre(empresa.getNombre())
            .direccion(empresa.getDireccion())
            .telefono(empresa.getTelefono())
            .email(empresa.getEmail())
            .activo(empresa.isActivo())
            .createdAt(empresa.getCreatedAt())
            .updatedAt(empresa.getUpdatedAt())
            .build();
    }
}
