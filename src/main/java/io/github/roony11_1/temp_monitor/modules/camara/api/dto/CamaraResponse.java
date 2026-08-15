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
    private Double temperaturaMin;
    private Double temperaturaMax;
    private boolean activo;
    private boolean eliminado;
    private Instant createdAt;
    private Instant updatedAt;
}
