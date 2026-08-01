package io.github.roony11_1.temp_monitor.modules.camara.api.dto.camara.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CamaraSummaryResponse 
{
    private Long id;
    private String nombre;
    private String descripcion;
    private Long sucursalId;
    private String sucursal;
    private Double temperaturaMin;
    private Double temperaturaMax;
    private boolean estado;
}
