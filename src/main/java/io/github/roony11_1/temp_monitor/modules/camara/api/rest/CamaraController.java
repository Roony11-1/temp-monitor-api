package io.github.roony11_1.temp_monitor.modules.camara.api.rest;

import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.application.CamaraService;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/camaras")
@RequiredArgsConstructor
public class CamaraController 
{
    private final CamaraService camaraService;

    @GetMapping
    public List<CamaraResponse> listarTodas() 
    {
        return camaraService.listarTodas().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/sucursal/{sucursalId}")
    public List<CamaraResponse> listarPorSucursal(@PathVariable Long sucursalId) 
    {
        return camaraService.listarPorSucursal(sucursalId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public CamaraResponse buscarPorId(@PathVariable Long id) 
    {
        return toResponse(camaraService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CamaraResponse> crear(@RequestBody CamaraRequest request) 
    {
        Camara camara = camaraService.crear(
                request.getNombre(),
                request.getDescripcion(),
                request.getSucursalId(),
                request.getTemperaturaMinima(),
                request.getTemperaturaMaxima()
        );
        return ResponseEntity.created(URI.create("/api/camaras/" + camara.getId()))
                .body(toResponse(camara));
    }

    @PutMapping("/{id}")
    public CamaraResponse actualizar(@PathVariable Long id, @RequestBody CamaraRequest request) 
    {
        return toResponse(camaraService.actualizar(
                id,
                request.getNombre(),
                request.getDescripcion(),
                request.getSucursalId(),
                request.getTemperaturaMinima(),
                request.getTemperaturaMaxima()
        ));
    }

    @PostMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long id) 
    {
        camaraService.activar(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) 
    {
        camaraService.desactivar(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) 
    {
        camaraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private CamaraResponse toResponse(Camara camara) 
    {
        CamaraResponse response = new CamaraResponse();
        response.setId(camara.getId());
        response.setNombre(camara.getNombre());
        response.setDescripcion(camara.getDescripcion());
        response.setSucursalId(camara.getSucursal().getId());
        response.setTemperaturaMinima(camara.getTemperaturaMinima());
        response.setTemperaturaMaxima(camara.getTemperaturaMaxima());
        response.setActivo(camara.isActivo());
        response.setCreatedAt(camara.getCreatedAt());
        response.setUpdatedAt(camara.getUpdatedAt());
        return response;
    }
}
