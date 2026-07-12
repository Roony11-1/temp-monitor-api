package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.roony11_1.temp_monitor.kernel.security.crypto.ApiKeyGenerator;
import io.github.roony11_1.temp_monitor.kernel.security.crypto.HashService;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.ActualizarSensorRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.AsignarSensorRequest;

import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistroSensorRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistroSensorResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.ApiKeyInvalidaException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.CamaraNotFoundException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorAlreadyExistsException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorNotFoundException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.SensorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensorService 
{
    private final SensorRepository sensorRepository;
    private final CamaraRepository camaraRepository;
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
    public Sensor asignar(AsignarSensorRequest request)
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

        return sensorRepository.save(sensor);
    }

    public Sensor buscarPorId(Long id)
    {
        return sensorRepository.findById(id)
            .orElseThrow(() -> new SensorNotFoundException("ID " + id));
    }

    @Transactional(readOnly = true)
    public List<Sensor> listarPorCamara(Long camaraId)
    {
        return sensorRepository.findByCamaraIdWithHierarchy(camaraId);
    }

    @Transactional(readOnly = true)
    public Sensor buscarPorUuid(UUID uuid)
    {
        return sensorRepository.findByUuidWithHierarchy(uuid)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + uuid));
    }

    @Transactional(readOnly = true)
    public List<Sensor> listarTodos()
    {
        return sensorRepository.findAllWithHierarchy();
    }

    @Transactional
    public Sensor actualizar(UUID uuid, ActualizarSensorRequest request)
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

        return sensorRepository.save(sensor);
    }

    public EstadoSensor consultarEstado(UUID uuid)
    {
        return sensorRepository.findByUuid(uuid)
            .map(Sensor::getEstado)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + uuid));
    }
}