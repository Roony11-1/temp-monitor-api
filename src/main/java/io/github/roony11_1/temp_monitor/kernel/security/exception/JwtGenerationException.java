package io.github.roony11_1.temp_monitor.kernel.security.exception;

import io.github.roony11_1.error.core.exceptions.AppException;
import io.github.roony11_1.temp_monitor.kernel.security.error.SecurityErrorCategories;

public class JwtGenerationException extends AppException 
{
    public JwtGenerationException(String detail) 
    {
        super("JWT-002", "Error al generar el token JWT: " + detail, SecurityErrorCategories.JWT_GENERATION_FAILED, "Error al generar el token JWT");
    }
}
