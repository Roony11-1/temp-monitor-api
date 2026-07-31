package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UltimaLecturaSensorResponse 
{
    private UUID sensorUuid;
    private Double temperatura;
    private Instant timestamp;
}
