package io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions;

import io.github.roony11_1.error.core.exceptions.AppException;

public class UserNotFoundException extends AppException 
{
    public UserNotFoundException(String email) 
    {
        super("USER-002", "Usuario no encontrado: " + email, UserErrorCategories.USER_NOT_FOUND, "Usuario no encontrado: " + email);
    }
}
