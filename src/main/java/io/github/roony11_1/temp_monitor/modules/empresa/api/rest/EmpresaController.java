package io.github.roony11_1.temp_monitor.modules.empresa.api.rest;

import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.EmpresaRequest;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.EmpresaResponse;
import io.github.roony11_1.temp_monitor.modules.empresa.core.application.EmpresaService;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController 
{
    private final EmpresaService empresaService;

    @GetMapping
    public List<EmpresaResponse> listarTodas() 
    {
        return empresaService.listarTodas().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public EmpresaResponse buscarPorId(@PathVariable Long id) 
    {
        return toResponse(empresaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<EmpresaResponse> crear(@RequestBody EmpresaRequest request) 
    {
        Empresa empresa = empresaService.crear(
                request.getNombre(),
                request.getDireccion(),
                request.getTelefono(),
                request.getEmail()
        );
        return ResponseEntity.created(URI.create("/api/empresas/" + empresa.getId()))
                .body(toResponse(empresa));
    }

    @PutMapping("/{id}")
    public EmpresaResponse actualizar(@PathVariable Long id, @RequestBody EmpresaRequest request) 
    {
        return toResponse(empresaService.actualizar(
                id,
                request.getNombre(),
                request.getDireccion(),
                request.getTelefono(),
                request.getEmail()
        ));
    }

    @PostMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long id) 
    {
        empresaService.activar(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) 
    {
        empresaService.desactivar(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) 
    {
        empresaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private EmpresaResponse toResponse(Empresa empresa) 
    {
        EmpresaResponse response = new EmpresaResponse();
        response.setId(empresa.getId());
        response.setNombre(empresa.getNombre());
        response.setDireccion(empresa.getDireccion());
        response.setTelefono(empresa.getTelefono());
        response.setEmail(empresa.getEmail());
        response.setActivo(empresa.isActivo());
        response.setCreatedAt(empresa.getCreatedAt());
        response.setUpdatedAt(empresa.getUpdatedAt());
        return response;
    }
}
