package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import java.time.Instant;
import java.util.UUID;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import lombok.Data;

@Data
public class SensorResponse 
{
    private Long id;
    private UUID uuid;
    private String apiKeyHash;
    private String macAddress;
    private CamaraResponse camara;
    private Long sucursalId;
    private String sucursalNombre;
    private Long empresaId;
    private String empresaNombre;
    private Instant ultimoContacto;
    private EstadoSensor estado;
    private Instant createdAt;
    private Instant updatedAt;
}
