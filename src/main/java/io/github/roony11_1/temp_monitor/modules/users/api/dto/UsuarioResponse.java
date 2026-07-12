package io.github.roony11_1.temp_monitor.modules.users.api.dto;

import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponse 
{
    private String email;
    private String nombre;

    public static UsuarioResponse toResponse(Usuario usuario) 
    {
        return UsuarioResponse.builder()
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .build();
    }
}
