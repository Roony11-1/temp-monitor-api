package io.github.roony11_1.temp_monitor.modules.empresa.api.rest;

import io.github.roony11_1.temp_monitor.kernel.dto.PageResponse;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.SucursalRequest;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.SucursalResponse;
import io.github.roony11_1.temp_monitor.modules.empresa.core.application.SucursalService;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public PageResponse<SucursalResponse> listarTodas(            @PageableDefault(size = 10000, sort = "id") Pageable pageable) 
    {
        var page = sucursalService.listarTodas(pageable)
                .map(SucursalResponse::toResponse);

        return PageResponse.from(page);
    }

    @GetMapping("/empresa/{empresaId}")
    public List<SucursalResponse> listarPorEmpresa(@PathVariable Long empresaId) 
    {
        return sucursalService.listarPorEmpresa(empresaId).stream()
                .map(SucursalResponse::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public SucursalResponse buscarPorId(@PathVariable Long id) 
    {
        var sucursal = sucursalService.buscarPorId(id);

        return SucursalResponse.toResponse(sucursal);
    }

    @PostMapping
    public ResponseEntity<SucursalResponse> crear(@RequestBody SucursalRequest request) 
    {
        Sucursal sucursal = sucursalService.crear(request);

        return ResponseEntity.created(URI.create("/api/sucursales/" + sucursal.getId()))
                .body(SucursalResponse.toResponse(sucursal));
    }

    @PutMapping("/{id}")
    public SucursalResponse actualizar(@PathVariable Long id, @RequestBody SucursalRequest request) 
    {
        var sucursal = sucursalService.actualizar(id, request);

        return SucursalResponse.toResponse(sucursal);
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
}
