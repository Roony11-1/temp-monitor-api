package io.github.roony11_1.temp_monitor.modules.users.api.rest;

import io.github.roony11_1.temp_monitor.kernel.dto.PageResponse;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.CambiarPasswordRequest;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioRequest;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioResponse;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.users.core.application.UsuarioService;
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
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController 
{
    private final UsuarioService usuarioService;

    @GetMapping
    public PageResponse<UsuarioSummaryResponse> listarTodos(
            @PageableDefault(size = 10000, sort = "id") Pageable pageable,
            @RequestParam Map<String, String> filters)
    {
        var page = usuarioService.listarTodos(pageable, filters);

        return PageResponse.from(page);
    }

    @GetMapping("/empresa/{empresaId}")
    public List<UsuarioSummaryResponse> listarPorEmpresa(@PathVariable Long empresaId) 
    {
        return usuarioService.listarPorEmpresaSummary(empresaId);
    }

    @GetMapping("/sucursal/{sucursalId}")
    public List<UsuarioSummaryResponse> listarPorSucursal(@PathVariable Long sucursalId) 
    {
        return usuarioService.listarPorSucursalSummary(sucursalId);
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable Long id) 
    {
        return usuarioService.buscarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'SUPER_ADMIN')")
    public ResponseEntity<UsuarioResponse> crear(@RequestBody UsuarioRequest request) 
    {
        UsuarioResponse usuario = usuarioService.crear(request);
        
        return ResponseEntity
            .created(URI.create("/api/usuarios/" + usuario.getId()))
            .body(usuario);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'SUPER_ADMIN')")
    public UsuarioResponse actualizar(@PathVariable Long id, @RequestBody UsuarioRequest request) 
    {
        return usuarioService.actualizar(id, request);
    }

    @PostMapping("/{id}/password")
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'SUPER_ADMIN')")
    public ResponseEntity<Void> cambiarPassword(@PathVariable Long id, @RequestBody CambiarPasswordRequest request) 
    {
        usuarioService.cambiarPassword(id, request.getNuevaPassword());
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'SUPER_ADMIN')")
    public ResponseEntity<Void> activar(@PathVariable Long id) 
    {
        usuarioService.activar(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'SUPER_ADMIN')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) 
    {
        usuarioService.desactivar(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/restaurar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UsuarioResponse> restaurar(@PathVariable Long id) 
    {
        return ResponseEntity.ok(usuarioService.restaurar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'SUPER_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) 
    {
        usuarioService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
