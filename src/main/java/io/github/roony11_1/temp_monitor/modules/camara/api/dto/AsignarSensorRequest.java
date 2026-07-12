package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AsignarSensorRequest 
{
    private UUID uuid;
    private String apiKey;
    private Long camaraId;
}
