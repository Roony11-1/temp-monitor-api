package io.github.roony11_1.temp_monitor.modules.empresa.api.dto;

import java.time.Instant;

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
    private boolean eliminado;
    private Instant createdAt;
    private Instant updatedAt;
}
