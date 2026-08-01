package io.github.roony11_1.temp_monitor.modules.users.api.dto;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSummaryResponse 
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
}
