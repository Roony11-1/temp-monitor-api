package io.github.roony11_1.temp_monitor.kernel.security.service;

import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;

public interface IUserCredentialsService 
{
    TokenUser authenticate(String email, String rawPassword);

    /**
     * Valida un usuario por id contra el estado actual en BD (no eliminado,
     * activo, empresa/sucursal activa) y lo devuelve con datos frescos
     * (roles y ámbito). Se usa por request en la autenticación JWT y al
     * renovar un access token (refresh).
     */
    TokenUser validateAndGetByUserId(Long userId);
}
