package io.github.roony11_1.temp_monitor.modules.users.api.rest;

import io.github.roony11_1.temp_monitor.modules.users.api.dto.CambiarPasswordRequest;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioRequest;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioResponse;
import io.github.roony11_1.temp_monitor.modules.users.core.application.UsuarioService;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController 
{
    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponse> listarTodos() 
    {
        return usuarioService.listarTodos().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/empresa/{empresaId}")
    public List<UsuarioResponse> listarPorEmpresa(@PathVariable Long empresaId) 
    {
        return usuarioService.listarPorEmpresa(empresaId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/sucursal/{sucursalId}")
    public List<UsuarioResponse> listarPorSucursal(@PathVariable Long sucursalId) 
    {
        return usuarioService.listarPorSucursal(sucursalId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable Long id) 
    {
        return toResponse(usuarioService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'SUPER_ADMIN')")
    public ResponseEntity<UsuarioResponse> crear(@RequestBody UsuarioRequest request) 
    {
        Usuario usuario = usuarioService.crear(
                request.getEmail(),
                request.getPassword(),
                request.getNombre(),
                request.getEmpresaId(),
                request.getSucursalId(),
                request.getRoles()
        );
        return ResponseEntity.created(URI.create("/api/usuarios/" + usuario.getId()))
                .body(toResponse(usuario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_EMPRESA', 'SUPER_ADMIN')")
    public UsuarioResponse actualizar(@PathVariable Long id, @RequestBody UsuarioRequest request) 
    {
        return toResponse(usuarioService.actualizar(
                id,
                request.getNombre(),
                request.getTelefono(),
                request.getEmpresaId(),
                request.getSucursalId(),
                request.getRoles()
        ));
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

    private UsuarioResponse toResponse(Usuario usuario) 
    {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setEmail(usuario.getEmail());
        response.setNombre(usuario.getNombre());
        response.setTelefono(usuario.getTelefono());
        response.setRoles(usuario.getRoles());
        response.setEmpresaId(usuario.getEmpresaId());
        response.setSucursalId(usuario.getSucursalId());
        response.setActivo(usuario.isActivo());
        response.setCreatedAt(usuario.getCreatedAt());
        response.setLastLogin(usuario.getLastLogin());
        return response;
    }
}
