package io.github.roony11_1.temp_monitor.kernel.security.model;

import java.util.Set;

public record TokenUser(
        Long id,
        String email,
        Set<Rol> roles,
        Long empresaId,
        Long sucursalId)
{
}
