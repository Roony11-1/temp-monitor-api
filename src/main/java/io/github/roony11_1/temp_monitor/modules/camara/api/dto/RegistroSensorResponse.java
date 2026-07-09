package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import java.util.UUID;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistroSensorResponse 
{
    private EstadoSensor estado;
    private UUID uuid;
    private String apiKey;
}
