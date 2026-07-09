package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistroSensorResponse 
{
    private UUID uuid;
    private String apiKey;
}
