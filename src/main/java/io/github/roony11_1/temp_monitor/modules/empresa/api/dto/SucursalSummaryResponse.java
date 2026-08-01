package io.github.roony11_1.temp_monitor.modules.empresa.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalSummaryResponse 
{
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String empresa;
    private Long empresaId;
    private boolean activo;
}
