package io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class EmailAlreadyExistsException extends AppException 
{
    public EmailAlreadyExistsException(String email) 
    {
        super("USER-001", "El email ya está registrado: " + email, StandardErrorCategories.ALREADY_EXISTS, "El email ya está registrado: " + email);
    }
}
