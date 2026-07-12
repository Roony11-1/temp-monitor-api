package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistrarLecturaRequest;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorDeshabilitadoException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorNotFoundException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Lectura;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.LecturaRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.SensorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LecturaService
{
    private final LecturaRepository lecturaRepository;
    private final SensorRepository sensorRepository;

    @Transactional
    public void registrar(UUID sensorUuid, RegistrarLecturaRequest request)
    {
        Sensor sensor = sensorRepository.findByUuid(sensorUuid)
            .orElseThrow(() -> new SensorNotFoundException("UUID " + sensorUuid));

        if (sensor.getEstado() == EstadoSensor.DESHABILITADO || sensor.getEstado() == EstadoSensor.PENDIENTE)
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

    @Transactional(readOnly = true)
    public List<Lectura> listarPorSensor(UUID sensorUuid)
    {
        return lecturaRepository.findBySensorUuidOrderByTimestampDesc(sensorUuid);
    }
}
