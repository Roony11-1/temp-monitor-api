package io.github.roony11_1.temp_monitor.modules.users.api.dto;

import java.time.Instant;
import java.util.List;

import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponse 
{
    private Long id;
    private String email;
    private String nombre;
    private String telefono;
    private String empresa;
    private Long empresaId;
    private String sucursal;
    private Long sucursalId;
    private List<String> roles;
    private boolean activo;
    private Instant createdAt;
    private Instant lastLogin;

    public static UsuarioResponse toResponse(Usuario usuario) 
    {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .telefono(usuario.getTelefono())
                .empresa(usuario.getEmpresa() != null ? usuario.getEmpresa().getNombre() : null)
                .empresaId(usuario.getEmpresaId())
                .sucursal(usuario.getSucursal() != null ? usuario.getSucursal().getNombre() : null)
                .sucursalId(usuario.getSucursalId())
                .roles(usuario.getRoles().stream().map(Enum::name).toList())
                .activo(usuario.isActivo())
                .createdAt(usuario.getCreatedAt())
                .lastLogin(usuario.getLastLogin())
                .build();
    }
}
