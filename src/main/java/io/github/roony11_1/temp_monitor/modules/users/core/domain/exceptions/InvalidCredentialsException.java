package io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class InvalidCredentialsException extends AppException 
{
    public InvalidCredentialsException() 
    {
        super("AUTH-001", "Credenciales inválidas", StandardErrorCategories.UNAUTHORIZED, "Credenciales inválidas");
    }
}
