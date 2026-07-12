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
                .map(EmpresaResponse::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public EmpresaResponse buscarPorId(@PathVariable Long id) 
    {
        var empresa = empresaService.buscarPorId(id);
        
        return EmpresaResponse.toResponse(empresa);
    }

    @PostMapping
    public ResponseEntity<EmpresaResponse> crear(@RequestBody EmpresaRequest request) 
    {
        Empresa empresa = empresaService.crear(request);

            return ResponseEntity.created(URI.create("/api/empresas/" + empresa.getId()))
                .body(EmpresaResponse.toResponse(empresa));
    }

    @PutMapping("/{id}")
    public EmpresaResponse actualizar(@PathVariable Long id, @RequestBody EmpresaRequest request) 
    {
        var empresa = empresaService.actualizar(id, request);

        return EmpresaResponse.toResponse(empresa);
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
}
