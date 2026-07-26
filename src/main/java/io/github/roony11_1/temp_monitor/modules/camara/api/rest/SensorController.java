package io.github.roony11_1.temp_monitor.modules.camara.api.rest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.roony11_1.temp_monitor.kernel.dto.PageResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.ActualizarSensorRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.AsignarSensorRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistroSensorRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistroSensorResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.SensorResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.application.SensorService;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sensores")
@RequiredArgsConstructor
public class SensorController 
{
    private final SensorService sensorService;

    @GetMapping
    public ResponseEntity<PageResponse<SensorResponse>> listarTodos(
            @PageableDefault(size = 10000, sort = "id") Pageable pageable,
            @RequestParam Map<String, String> filters)
    {
        var page = sensorService.listarTodos(pageable, filters);

        return ResponseEntity.ok(PageResponse.from(page));
    }

    @PostMapping("/registrar")
    public ResponseEntity<RegistroSensorResponse> registrar(@RequestBody RegistroSensorRequest request)
    {
        RegistroSensorResponse response = sensorService.registrar(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/asignar")
    public ResponseEntity<SensorResponse> asignar(@RequestBody AsignarSensorRequest request)
    {
        var response = SensorResponse.toResponse(sensorService.asignar(request));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/camara/{camaraId}")
    public ResponseEntity<List<SensorResponse>> listarPorCamara(@PathVariable Long camaraId)
    {
        List<SensorResponse> sensores = sensorService.listarPorCamara(camaraId).stream()
            .map(SensorResponse::toResponse)
            .toList();

        return ResponseEntity.ok(sensores);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<SensorResponse> actualizar(@PathVariable UUID uuid, @RequestBody ActualizarSensorRequest request)
    {
        var response = SensorResponse.toResponse(sensorService.actualizar(uuid, request));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uuid}/estado")
    public ResponseEntity<EstadoSensor> consultarEstado(@PathVariable UUID uuid)
    {
        EstadoSensor estado = sensorService.consultarEstado(uuid);
        
        return ResponseEntity.ok(estado);
    }
}
