package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActualizarSensorRequest 
{
    private EstadoSensor estado;
    private Long camaraId;
}
