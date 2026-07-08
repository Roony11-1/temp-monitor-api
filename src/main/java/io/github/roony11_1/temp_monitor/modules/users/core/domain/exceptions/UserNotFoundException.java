package io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class UserNotFoundException extends AppException 
{
    public UserNotFoundException(String email) 
    {
        super("AUTH-003", "Usuario no encontrado: " + email, StandardErrorCategories.NOT_FOUND, "Usuario no encontrado: " + email);
    }
}
