package io.github.roony11_1.temp_monitor.kernel.security.exception;

import io.github.roony11_1.error.core.exceptions.AppException;
import io.github.roony11_1.temp_monitor.kernel.security.error.SecurityErrorCategories;

public class InvalidTokenUserException extends AppException 
{
    public InvalidTokenUserException(String detail) 
    {
        super("JWT-001", "TokenUser inválido: " + detail, SecurityErrorCategories.INVALID_TOKEN_USER, "TokenUser inválido: " + detail);
    }
}
