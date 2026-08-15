package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistrarLecturaRequest;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorDeshabilitadoException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorNotFoundException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorSinCamaraException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.GranularidadLectura;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Lectura;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.LecturaResumen;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.LecturaRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.LecturaResumenRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LecturaService
{
    private final LecturaRepository lecturaRepository;
    private final LecturaResumenRepository lecturaResumenRepository;
    private final SensorRepository sensorRepository;

    @Transactional
    public void registrar(UUID sensorUuid, RegistrarLecturaRequest request)
    {
        Sensor sensor = sensorRepository.findActiveByUuid(sensorUuid)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + sensorUuid));

        if (sensor.getCamara() == null)
        {
            throw new SensorSinCamaraException(sensorUuid.toString());
        }

        if (sensor.puedeRegistrarLectura())
        {
            throw new SensorDeshabilitadoException(sensorUuid.toString());
        }

        var now = Instant.now();

        Lectura lectura = Lectura.builder()
            .sensorUuid(sensorUuid)
            .temperatura(request.getTemperatura())
            .timestamp(now)
            .build();

        lecturaRepository.save(lectura);
        
        sensor.setUltimoContacto(now);

        log.info("Lectura registrada: sensor={}, temperatura={}°C, timestamp={}, camara={}",
                sensorUuid, request.getTemperatura(), now,
                sensor.getCamara() != null ? sensor.getCamara().getId() : null);
    }

    @Transactional(readOnly = true)
    public Page<Lectura> listarPorSensor(UUID sensorUuid, Pageable pageable)
    {
        return lecturaRepository.findBySensorUuidOrderByTimestampDesc(sensorUuid, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Lectura> listarPorSensor(UUID sensorUuid, Instant since, Pageable pageable)
    {
        return lecturaRepository.findBySensorUuidAndTimestampAfterOrderByTimestampDesc(sensorUuid, since, pageable);
    }

    @Transactional(readOnly = true)
    public Page<LecturaResumen> listarResumenPorSensor(UUID sensorUuid, GranularidadLectura granularidad, Pageable pageable)
    {
        return lecturaResumenRepository.findBySensorUuidAndGranularidadOrderByBucketStartDesc(sensorUuid, granularidad, pageable);
    }
}
