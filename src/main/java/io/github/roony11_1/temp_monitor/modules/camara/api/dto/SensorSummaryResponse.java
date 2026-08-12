package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import java.util.UUID;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorSummaryResponse 
{
    private Long id;
    private UUID uuid;
    private String macAddress;
    private Long camaraId;
    private String camaraNombre;
    private Long sucursalId;
    private String sucursalNombre;
    private Long empresaId;
    private String empresaNombre;
    private EstadoSensor estado;
    private boolean eliminado;
}
