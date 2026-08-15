package io.github.roony11_1.temp_monitor.modules.empresa.api.dto;

import java.time.Instant;

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
    private boolean eliminado;
    private Instant createdAt;
    private Instant updatedAt;
}
