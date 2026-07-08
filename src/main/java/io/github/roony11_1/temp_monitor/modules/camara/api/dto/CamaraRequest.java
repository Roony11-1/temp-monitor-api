package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import lombok.Data;

@Data
public class CamaraRequest 
{
    private String nombre;
    private String descripcion;
    private Long sucursalId;
    private Double temperaturaMinima;
    private Double temperaturaMaxima;
}
