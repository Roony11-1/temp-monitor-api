package io.github.roony11_1.temp_monitor.modules.empresa.api.rest;

import io.github.roony11_1.temp_monitor.kernel.dto.PageResponse;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.EmpresaRequest;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.EmpresaResponse;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.EmpresaSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.empresa.core.application.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController 
{
    private final EmpresaService empresaService;

    @GetMapping
    public PageResponse<EmpresaSummaryResponse> listarTodas(
            @PageableDefault(size = 10000, sort = "id") Pageable pageable,
            @RequestParam Map<String, String> filters)
    {
        var page = empresaService.listarTodas(pageable, filters);

        return PageResponse.from(page);
    }

    @GetMapping("/{id}")
    public EmpresaResponse buscarPorId(@PathVariable Long id) 
    {
        return empresaService.buscarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<EmpresaResponse> crear(@RequestBody EmpresaRequest request) 
    {
        EmpresaResponse empresa = empresaService.crear(request);

            return ResponseEntity.created(URI.create("/api/empresas/" + empresa.getId()))
                .body(empresa);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public EmpresaResponse actualizar(@PathVariable Long id, @RequestBody EmpresaRequest request) 
    {
        return empresaService.actualizar(id, request);
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> activar(@PathVariable Long id) 
    {
        empresaService.activar(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) 
    {
        empresaService.desactivar(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/restaurar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<EmpresaResponse> restaurar(@PathVariable Long id) 
    {
        return ResponseEntity.ok(empresaService.restaurar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) 
    {
        empresaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
