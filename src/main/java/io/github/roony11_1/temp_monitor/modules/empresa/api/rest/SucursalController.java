package io.github.roony11_1.temp_monitor.modules.empresa.api.rest;

import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.SucursalRequest;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.SucursalResponse;
import io.github.roony11_1.temp_monitor.modules.empresa.core.application.SucursalService;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class SucursalController 
{
    private final SucursalService sucursalService;

    @GetMapping
    public List<SucursalResponse> listarTodas() 
    {
        return sucursalService.listarTodas().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/empresa/{empresaId}")
    public List<SucursalResponse> listarPorEmpresa(@PathVariable Long empresaId) 
    {
        return sucursalService.listarPorEmpresa(empresaId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public SucursalResponse buscarPorId(@PathVariable Long id) {
        return toResponse(sucursalService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<SucursalResponse> crear(@RequestBody SucursalRequest request) 
    {
        Sucursal sucursal = sucursalService.crear(
                request.getNombre(),
                request.getDireccion(),
                request.getTelefono(),
                request.getEmpresaId()
        );
        return ResponseEntity.created(URI.create("/api/sucursales/" + sucursal.getId()))
                .body(toResponse(sucursal));
    }

    @PutMapping("/{id}")
    public SucursalResponse actualizar(@PathVariable Long id, @RequestBody SucursalRequest request) 
    {
        return toResponse(sucursalService.actualizar(
                id,
                request.getNombre(),
                request.getDireccion(),
                request.getTelefono(),
                request.getEmpresaId()
        ));
    }

    @PostMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long id) 
    {
        sucursalService.activar(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) 
    {
        sucursalService.desactivar(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) 
    {
        sucursalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private SucursalResponse toResponse(Sucursal sucursal) 
    {
        SucursalResponse response = new SucursalResponse();
        response.setId(sucursal.getId());
        response.setNombre(sucursal.getNombre());
        response.setDireccion(sucursal.getDireccion());
        response.setTelefono(sucursal.getTelefono());
        response.setEmpresaId(sucursal.getEmpresa().getId());
        response.setActivo(sucursal.isActivo());
        response.setCreatedAt(sucursal.getCreatedAt());
        response.setUpdatedAt(sucursal.getUpdatedAt());
        return response;
    }
}
