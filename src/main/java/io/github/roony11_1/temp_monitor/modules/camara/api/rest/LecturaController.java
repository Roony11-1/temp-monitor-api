package io.github.roony11_1.temp_monitor.modules.camara.api.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistrarLecturaRequest;
import io.github.roony11_1.temp_monitor.modules.camara.core.application.LecturaService;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Lectura;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lecturas")
@RequiredArgsConstructor
public class LecturaController
{
    private final LecturaService lecturaService;

    @PostMapping("/sensor/{uuid}")
    public ResponseEntity<Void> registrar(@PathVariable UUID uuid, @RequestBody RegistrarLecturaRequest request)
    {
        lecturaService.registrar(uuid, request);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sensor/{uuid}")
    public ResponseEntity<List<Lectura>> listarPorSensor(@PathVariable UUID uuid)
    {
        return ResponseEntity.ok(lecturaService.listarPorSensor(uuid));
    }
}
