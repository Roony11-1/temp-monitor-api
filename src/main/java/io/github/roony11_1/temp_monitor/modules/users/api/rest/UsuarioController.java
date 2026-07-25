package io.github.roony11_1.temp_monitor.modules.users.api.rest;

import io.github.roony11_1.temp_monitor.kernel.dto.PageResponse;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.CambiarPasswordRequest;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioRequest;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioResponse;
import io.github.roony11_1.temp_monitor.modules.users.core.application.UsuarioService;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
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
    public PageResponse<UsuarioResponse> listarTodos(
            @PageableDefault(size = 10000, sort = "id") Pageable pageable,
            @RequestParam Map<String, String> filters)
    {
        var page = usuarioService.listarTodos(pageable, filters)
            .map(UsuarioResponse::toResponse);

        return PageResponse.from(page);
    }

    @GetMapping("/empresa/{empresaId}")
    public List<UsuarioResponse> listarPorEmpresa(@PathVariable Long empresaId) 
    {
        return usuarioService.listarPorEmpresa(empresaId).stream()
            .map(UsuarioResponse::toResponse)
            .toList();
    }

    @GetMapping("/sucursal/{sucursalId}")
    public List<UsuarioResponse> listarPorSucursal(@PathVariable Long sucursalId) 
    {
        var response = usuarioService.listarPorSucursal(sucursalId).stream()
            .map(UsuarioResponse::toResponse)
            .toList();

        return response;
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable Long id) 
    {
        var usuario = usuarioService.buscarPorId(id);

        return UsuarioResponse.toResponse(usuario);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'SUPER_ADMIN')")
    public ResponseEntity<UsuarioResponse> crear(@RequestBody UsuarioRequest request) 
    {
        Usuario usuario = usuarioService.crear(request);
        
        return ResponseEntity
            .created(URI.create("/api/usuarios/" + usuario.getId()))
            .body(UsuarioResponse.toResponse(usuario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'SUPER_ADMIN')")
    public UsuarioResponse actualizar(@PathVariable Long id, @RequestBody UsuarioRequest request) 
    {
        var usuario = usuarioService.actualizar(id, request);

        return UsuarioResponse.toResponse(usuario);
    }

    @PostMapping("/{id}/password")
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'SUPER_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) 
    {
        usuarioService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
