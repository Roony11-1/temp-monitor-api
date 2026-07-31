package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import lombok.Data;

import java.time.Instant;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;

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
    private Instant createdAt;
    private Instant updatedAt;

    public static CamaraResponse toResponse(Camara camara) 
    {
        CamaraResponse response = new CamaraResponse();
        response.setId(camara.getId());
        response.setNombre(camara.getNombre());
        response.setDescripcion(camara.getDescripcion());
        response.setSucursalId(camara.getSucursal().getId());
        response.setTemperaturaMin(camara.getTemperaturaMin());
        response.setTemperaturaMax(camara.getTemperaturaMax());
        response.setActivo(camara.isActivo());
        response.setCreatedAt(camara.getCreatedAt());
        response.setUpdatedAt(camara.getUpdatedAt());

        return response;
    }
}
