package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.roony11_1.temp_monitor.kernel.mapper.DetailEntityMapper;
import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.kernel.security.crypto.ApiKeyGenerator;
import io.github.roony11_1.temp_monitor.kernel.security.crypto.HashService;
import io.github.roony11_1.temp_monitor.kernel.security.scope.CurrentUserScope;
import io.github.roony11_1.specification.core.FilterCondition;
import io.github.roony11_1.specification.core.FilterOperator;
import io.github.roony11_1.specification.spring.FilterSpecificationBuilder;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.ActualizarSensorRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.AsignarSensorRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistroSensorRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistroSensorResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.SensorResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.SensorSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.ApiKeyInvalidaException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.CamaraNotFoundException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorAlreadyExistsException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorNotFoundException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.SensorRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensorService 
{
    private static final Map<String, String> FILTER_ALIASES = Map.of(
            "empresaNombre", "camara.sucursal.empresa.nombre",
            "sucursalNombre", "camara.sucursal.nombre");

    private final SensorRepository sensorRepository;
    private final CamaraRepository camaraRepository;
    private final HashService hashService;
    private final ApiKeyGenerator apiKeyGenerator;
    private final EntityMapper<Sensor, SensorSummaryResponse> sensorMapper;
    private final DetailEntityMapper<Sensor, SensorResponse> sensorDetailMapper;
    private final CurrentUserScope currentUserScope;

    public Optional<Sensor> authenticateByUuidAndApiKey(UUID uuid, String apiKey) 
    {
        return sensorRepository.findActiveByUuid(uuid)
            .filter(sensor -> hashService.verify(apiKey, sensor.getApiKeyHash()));
    }

    @Transactional
    public RegistroSensorResponse registrar(RegistroSensorRequest request)
    {
        if (sensorRepository.findActiveByMacAddress(request.getMacAddress()).isPresent())
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
        Sensor sensor = sensorRepository.findActiveByUuid(request.getUuid())
            .orElseThrow(() -> new SensorNotFoundException("UUID " + request.getUuid()));

        if (!hashService.verify(request.getApiKey(), sensor.getApiKeyHash()))
        {
            throw new ApiKeyInvalidaException();
        }

        Camara camara = camaraRepository.findById(request.getCamaraId())
            .orElseThrow(() -> new CamaraNotFoundException("ID " + request.getCamaraId()));

        if (camara.getDeletedAt() != null)
        {
            throw new CamaraNotFoundException("ID " + request.getCamaraId());
        }

        currentUserScope.assertAccess(camara.getSucursal().getId(), camara.getSucursal().getEmpresa().getId());

        sensor.setCamara(camara);
        sensor.setEstado(EstadoSensor.ACTIVO);
        sensor.setEstadoPrevio(null);

        return sensorDetailMapper.toResponse(sensor);
    }

    @Transactional(readOnly = true)
    public List<SensorResponse> listarPorCamara(Long camaraId)
    {
        assertCamaraEnScope(camaraId);
        return sensorRepository.findByCamaraIdWithHierarchy(camaraId).stream()
                .map(sensorDetailMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SensorResponse buscarPorUuid(UUID uuid)
    {
        Sensor sensor = sensorRepository.findByUuidWithHierarchy(uuid)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + uuid));

        currentUserScope.assertAccess(sucursalIdOf(sensor), empresaIdOf(sensor));

        return sensorDetailMapper.toResponse(sensor);
    }

    @Transactional(readOnly = true)
    public Page<SensorSummaryResponse> listarTodos(Pageable pageable, Map<String, String> filters)
    {
        var userSpec = new FilterSpecificationBuilder<Sensor>()
                .withAliases(FILTER_ALIASES)
                .withConditions(filters)
                .build();
        return sensorRepository.findAll(scopeSpec().and(userSpec), pageable)
                .map(sensorMapper::toSummaryResponse);
    }

    @Transactional
    public SensorResponse actualizar(UUID uuid, ActualizarSensorRequest request)
    {
        Sensor sensor = sensorRepository.findActiveByUuidWithHierarchy(uuid)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + uuid));

        if (request.getCamaraId() != null)
        {
            Camara camara = camaraRepository.findById(request.getCamaraId())
                .orElseThrow(() -> new CamaraNotFoundException("ID " + request.getCamaraId()));

            if (camara.getDeletedAt() != null)
            {
                throw new CamaraNotFoundException("ID " + request.getCamaraId());
            }

            currentUserScope.assertAccess(camara.getSucursal().getId(), camara.getSucursal().getEmpresa().getId());
            sensor.setCamara(camara);

            if (sensor.getEstado() == EstadoSensor.PENDIENTE && request.getEstado() == null)
            {
                sensor.setEstado(EstadoSensor.ACTIVO);
                sensor.setEstadoPrevio(null);
            }
        }

        if (request.getEstado() != null)
        {
            if (request.getEstado() == EstadoSensor.PENDIENTE)
            {
                throw new IllegalArgumentException("No se puede asignar el estado PENDIENTE manualmente");
            }
            sensor.setEstado(request.getEstado());
            sensor.setEstadoPrevio(null);
        }

        Hibernate.initialize(sensor.getCamara().getSucursal());
        Hibernate.initialize(sensor.getCamara().getSucursal().getEmpresa());
        
        return sensorDetailMapper.toResponse(sensor);
    }

    public EstadoSensor consultarEstado(UUID uuid)
    {
        return sensorRepository.findActiveByUuid(uuid)
            .map(Sensor::getEstado)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + uuid));
    }

    @Transactional
    public RegistroSensorResponse renewApiKey(UUID uuid)
    {
        Sensor sensor = sensorRepository.findActiveByUuid(uuid)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + uuid));

        String newApiKey = apiKeyGenerator.generate();
        sensor.setApiKeyHash(hashService.hash(newApiKey));

        return RegistroSensorResponse.builder()
            .uuid(sensor.getUuid())
            .estado(sensor.getEstado())
            .apiKey(newApiKey)
            .build();
    }

    @Transactional
    public RegistroSensorResponse renewApiKeyByMac(String macAddress)
    {
        Sensor sensor = sensorRepository.findActiveByMacAddress(macAddress)
            .orElseThrow(() -> new SensorNotFoundException("MAC " + macAddress));

        String newApiKey = apiKeyGenerator.generate();
        sensor.setApiKeyHash(hashService.hash(newApiKey));

        return RegistroSensorResponse.builder()
            .uuid(sensor.getUuid())
            .estado(sensor.getEstado())
            .apiKey(newApiKey)
            .build();
    }

    @Transactional
    public void eliminar(UUID uuid)
    {
        Sensor sensor = sensorRepository.findActiveByUuidWithHierarchy(uuid)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + uuid));

        sensor.setDeletedAt(Instant.now());
    }

    @Transactional
    public SensorResponse restaurar(UUID uuid)
    {
        Sensor sensor = sensorRepository.findByUuidWithHierarchy(uuid)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + uuid));

        sensor.setDeletedAt(null);

        return sensorDetailMapper.toResponse(sensor);
    }

    private Specification<Sensor> scopeSpec()
    {
        return currentUserScope.scopeSpec("camara.sucursal.empresa.id", "camara.sucursal.id");
    }

    private void assertCamaraEnScope(Long camaraId)
    {
        var byId = new FilterSpecificationBuilder<Camara>()
                .withCondition(new FilterCondition("id", FilterOperator.EQ, camaraId))
                .build();
        camaraRepository.findOne(currentUserScope.<Camara>scopeSpec("sucursal.empresa.id", "sucursal.id").and(byId))
            .orElseThrow(() -> new CamaraNotFoundException("ID " + camaraId));
    }

    private Long sucursalIdOf(Sensor sensor)
    {
        if (sensor.getCamara() == null) return null;
        Sucursal sucursal = sensor.getCamara().getSucursal();
        return sucursal != null ? sucursal.getId() : null;
    }

    private Long empresaIdOf(Sensor sensor)
    {
        if (sensor.getCamara() == null) return null;
        Sucursal sucursal = sensor.getCamara().getSucursal();
        return sucursal != null && sucursal.getEmpresa() != null ? sucursal.getEmpresa().getId() : null;
    }
}