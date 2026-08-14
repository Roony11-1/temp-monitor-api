package io.github.roony11_1.temp_monitor.modules.camara.api.rest;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.roony11_1.temp_monitor.kernel.dto.PageResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.RegistrarLecturaRequest;
import io.github.roony11_1.temp_monitor.modules.camara.core.application.LecturaService;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.GranularidadLectura;
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
    public ResponseEntity<PageResponse<?>> listarPorSensor(
            @PathVariable UUID uuid,
            @RequestParam(required = false) Long since,
            @RequestParam(required = false) GranularidadLectura granularidad,
            @PageableDefault(size = 10000, sort = "timestamp") Pageable pageable)
    {
        if (granularidad != null)
        {
            Pageable resumenPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

            return ResponseEntity.ok(PageResponse.from(
                    lecturaService.listarResumenPorSensor(uuid, granularidad, resumenPageable)));
        }

        var page = since != null
            ? lecturaService.listarPorSensor(uuid, Instant.ofEpochMilli(since), pageable)
            : lecturaService.listarPorSensor(uuid, pageable);

        return ResponseEntity.ok(PageResponse.from(page));
    }
}
