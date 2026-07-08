package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CamaraResponse 
{
    private Long id;
    private String nombre;
    private String descripcion;
    private Long sucursalId;
    private Double temperaturaMinima;
    private Double temperaturaMaxima;
    private boolean activo;
    private Instant createdAt;
    private Instant updatedAt;
}
