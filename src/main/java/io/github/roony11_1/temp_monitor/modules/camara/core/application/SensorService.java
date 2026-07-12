package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.roony11_1.temp_monitor.kernel.security.crypto.ApiKeyGenerator;
import io.github.roony11_1.temp_monitor.kernel.security.crypto.HashService;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.ActualizarSensorRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.AsignarSensorRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraResponse;

import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistrarLecturaRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistroSensorRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistroSensorResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.SensorResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.ApiKeyInvalidaException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.CamaraNotFoundException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorAlreadyExistsException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorDeshabilitadoException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorNotFoundException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Lectura;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.LecturaRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.SensorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensorService 
{
    private final SensorRepository sensorRepository;
    private final CamaraRepository camaraRepository;
    private final LecturaRepository lecturaRepository;
    private final HashService hashService;
    private final ApiKeyGenerator apiKeyGenerator;

    @Transactional
    public RegistroSensorResponse registrar(RegistroSensorRequest request)
    {
        if (sensorRepository.findByMacAddress(request.getMacAddress()).isPresent())
        {
            throw new SensorAlreadyExistsException(request.getMacAddress());
        }

        String apiKey = apiKeyGenerator.generate();

        var sensor = Sensor.builder()
            .uuid(UUID.randomUUID())
            .apiKeyHash(hashService.hash(apiKey))
            .macAddress(request.getMacAddress())
            .estado(EstadoSensor.PENDIENTE)
            .build();

        var sensorPersisted = sensorRepository.save(sensor);

        return RegistroSensorResponse.builder()
            .uuid(sensorPersisted.getUuid())
            .estado(sensorPersisted.getEstado())
            .apiKey(apiKey)
            .build();
    }

    @Transactional
    public SensorResponse asignar(AsignarSensorRequest request)
    {
        Sensor sensor = sensorRepository.findByUuid(request.getUuid())
            .orElseThrow(() -> new SensorNotFoundException("UUID " + request.getUuid()));

        if (!hashService.verify(request.getApiKey(), sensor.getApiKeyHash()))
        {
            throw new ApiKeyInvalidaException();
        }

        Camara camara = camaraRepository.findById(request.getCamaraId())
            .orElseThrow(() -> new CamaraNotFoundException("ID " + request.getCamaraId()));

        sensor.setCamara(camara);
        sensor.setEstado(EstadoSensor.ACTIVO);

        return toResponse(sensorRepository.save(sensor));
    }

    public Sensor buscarPorId(Long id)
    {
        return sensorRepository.findById(id)
            .orElseThrow(() -> new SensorNotFoundException("ID " + id));
    }

    @Transactional(readOnly = true)
    public List<SensorResponse> listarPorCamara(Long camaraId)
    {
        return sensorRepository.findByCamaraIdWithHierarchy(camaraId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public SensorResponse buscarPorUuid(UUID uuid)
    {
        return toResponse(sensorRepository.findByUuidWithHierarchy(uuid)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + uuid)));
    }

    @Transactional(readOnly = true)
    public List<SensorResponse> listarTodos()
    {
        return sensorRepository.findAllWithHierarchy().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public SensorResponse actualizar(UUID uuid, ActualizarSensorRequest request)
    {
        Sensor sensor = sensorRepository.findByUuid(uuid)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + uuid));

        if (request.getCamaraId() != null)
        {
            Camara camara = camaraRepository.findById(request.getCamaraId())
                .orElseThrow(() -> new CamaraNotFoundException("ID " + request.getCamaraId()));
            sensor.setCamara(camara);

            if (sensor.getEstado() == EstadoSensor.PENDIENTE && request.getEstado() == null)
            {
                sensor.setEstado(EstadoSensor.ACTIVO);
            }
        }

        if (request.getEstado() != null)
        {
            if (request.getEstado() == EstadoSensor.PENDIENTE)
            {
                throw new IllegalArgumentException("No se puede asignar el estado PENDIENTE manualmente");
            }
            sensor.setEstado(request.getEstado());
        }

        return toResponse(sensorRepository.save(sensor));
    }

    public EstadoSensor consultarEstado(UUID uuid)
    {
        return sensorRepository.findByUuid(uuid)
            .map(Sensor::getEstado)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + uuid));
    }

    @Transactional
    public void registrarLectura(UUID sensorUuid, RegistrarLecturaRequest request)
    {
        Sensor sensor = sensorRepository.findByUuid(sensorUuid)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + sensorUuid));

        if (sensor.getEstado() == EstadoSensor.DESHABILITADO)
        {
            throw new SensorDeshabilitadoException(sensorUuid.toString());
        }

        Lectura lectura = Lectura.builder()
            .sensorUuid(sensorUuid)
            .temperatura(request.getTemperatura())
            .build();

        lecturaRepository.save(lectura);
        sensor.setUltimoContacto(lectura.getTimestamp());
        sensorRepository.save(sensor);
    }

    public List<Lectura> listarLecturas(UUID sensorUuid)
    {
        return lecturaRepository.findBySensorUuidOrderByTimestampDesc(sensorUuid);
    }

    private SensorResponse toResponse(Sensor sensor)
    {
        SensorResponse response = new SensorResponse();
        response.setId(sensor.getId());
        response.setUuid(sensor.getUuid());
        response.setApiKeyHash(sensor.getApiKeyHash());
        response.setMacAddress(sensor.getMacAddress());
        response.setUltimoContacto(sensor.getUltimoContacto());
        response.setEstado(sensor.getEstado());
        response.setCreatedAt(sensor.getCreatedAt());
        response.setUpdatedAt(sensor.getUpdatedAt());

        if (sensor.getCamara() != null)
        {
            Camara camara = sensor.getCamara();
            CamaraResponse camaraResponse = new CamaraResponse();
            camaraResponse.setId(camara.getId());
            camaraResponse.setNombre(camara.getNombre());
            camaraResponse.setDescripcion(camara.getDescripcion());
            camaraResponse.setSucursalId(camara.getSucursal().getId());
            camaraResponse.setActivo(camara.isActivo());
            camaraResponse.setCreatedAt(camara.getCreatedAt());
            camaraResponse.setUpdatedAt(camara.getUpdatedAt());
            response.setCamara(camaraResponse);
            response.setSucursalId(camara.getSucursal().getId());
            response.setSucursalNombre(camara.getSucursal().getNombre());
            response.setEmpresaId(camara.getSucursal().getEmpresa().getId());
            response.setEmpresaNombre(camara.getSucursal().getEmpresa().getNombre());
        }

        return response;
    }
}