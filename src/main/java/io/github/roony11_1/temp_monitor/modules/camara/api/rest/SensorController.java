package io.github.roony11_1.temp_monitor.modules.camara.api.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistroSensorRequest;
import io.github.roony11_1.temp_monitor.modules.camara.core.application.SensorService;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sensores")
@RequiredArgsConstructor
public class SensorController 
{
    private final SensorService sensorService;

    @PostMapping("/registrar")
    public ResponseEntity<Sensor> registrar(@RequestBody RegistroSensorRequest request)
    {
        Sensor sensor = sensorService.registrar(request);
        
        return ResponseEntity.ok(sensor);
    }
}
