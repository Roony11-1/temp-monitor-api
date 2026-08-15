package io.github.roony11_1.temp_monitor.modules.camara.api.rest;

import io.github.roony11_1.temp_monitor.kernel.dto.PageResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraTemperaturaResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.UltimaLecturaSensorResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.camara.response.CamaraSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.application.CamaraLecturaService;
import io.github.roony11_1.temp_monitor.modules.camara.core.application.CamaraService;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.GranularidadLectura;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/camaras")
@RequiredArgsConstructor
public class CamaraController 
{
    private final CamaraService camaraService;
    private final CamaraLecturaService camaraLecturaService;

    @GetMapping
    public PageResponse<CamaraSummaryResponse> listarTodas(
            @PageableDefault(size = 10000, sort = "id") Pageable pageable,
            @RequestParam Map<String, String> filters)
    {
        var page = camaraService.listarTodas(pageable, filters);

        return PageResponse.from(page);
    }

    @GetMapping("/sucursal/{sucursalId}")
    public List<CamaraResponse> listarPorSucursal(@PathVariable Long sucursalId) 
    {
        return camaraService.listarPorSucursal(sucursalId);
    }

    @GetMapping("/{id}")
    public CamaraResponse buscarPorId(@PathVariable Long id) 
    {
        return camaraService.buscarPorId(id);
    }

    @GetMapping("/{id}/temperatura")
    public CamaraTemperaturaResponse obtenerTemperatura(@PathVariable Long id) 
    {
        return camaraService.obtenerTemperatura(id);
    }

    @GetMapping("/{id}/ultimas-lecturas")
    public List<UltimaLecturaSensorResponse> obtenerUltimasMedidas(@PathVariable Long id) 
    {
        return camaraService.obtenerUltimasMedidas(id);
    }

    @GetMapping("/{id}/lecturas")
    public List<?> listarLecturas(
            @PathVariable Long id,
            @RequestParam(required = false) Long desde,
            @RequestParam(required = false) GranularidadLectura granularidad)
    {
        if (granularidad != null)
        {
            return camaraLecturaService.listarResumenPorCamara(id, granularidad);
        }

        if (desde != null)
        {
            return camaraLecturaService.listarPorCamara(id, Instant.ofEpochMilli(desde));
        }

        return camaraLecturaService.listarTodoPorCamara(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'ADMIN_SUCURSAL', 'SUPER_ADMIN')")
    public ResponseEntity<CamaraResponse> crear(@RequestBody CamaraRequest request) 
    {
        CamaraResponse camara = camaraService.crear(request);

        return ResponseEntity.created(URI.create("/api/camaras/" + camara.getId()))
                .body(camara);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'ADMIN_SUCURSAL', 'SUPER_ADMIN')")
    public CamaraResponse actualizar(@PathVariable Long id, @RequestBody CamaraRequest request) 
    {
        return camaraService.actualizar(id, request);
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'ADMIN_SUCURSAL', 'SUPER_ADMIN')")
    public ResponseEntity<Void> activar(@PathVariable Long id) 
    {
        camaraService.activar(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'ADMIN_SUCURSAL', 'SUPER_ADMIN')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) 
    {
        camaraService.desactivar(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/restaurar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CamaraResponse> restaurar(@PathVariable Long id) 
    {
        return ResponseEntity.ok(camaraService.restaurar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'ADMIN_SUCURSAL', 'SUPER_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) 
    {
        camaraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
