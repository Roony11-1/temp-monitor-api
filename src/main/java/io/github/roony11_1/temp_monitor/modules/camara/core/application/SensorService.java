package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import org.springframework.stereotype.Service;

import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistroSensorResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.SensorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensorService 
{
    private final SensorRepository sensorRepository;

    public RegistroSensorResponse registrar()
    {
        return RegistroSensorResponse.builder().build();
    }
}
