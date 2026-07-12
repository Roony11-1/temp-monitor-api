package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import java.time.Instant;
import java.util.UUID;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;
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

    public static SensorResponse toResponse(Sensor sensor) 
    {
        SensorResponse response = new SensorResponse();
        response.setId(sensor.getId());
        response.setUuid(sensor.getUuid());
        response.setApiKeyHash(sensor.getApiKeyHash());
        response.setMacAddress(sensor.getMacAddress());
        response.setCamara(CamaraResponse.toResponse(sensor.getCamara()));
        response.setSucursalId(sensor.getCamara().getSucursal().getId());
        response.setSucursalNombre(sensor.getCamara().getSucursal().getNombre());
        response.setEmpresaId(sensor.getCamara().getSucursal().getEmpresa().getId());
        response.setEmpresaNombre(sensor.getCamara().getSucursal().getEmpresa().getNombre());
        response.setUltimoContacto(sensor.getUltimoContacto());
        response.setEstado(sensor.getEstado());
        response.setCreatedAt(sensor.getCreatedAt());
        response.setUpdatedAt(sensor.getUpdatedAt());

        return response;
    }
}
