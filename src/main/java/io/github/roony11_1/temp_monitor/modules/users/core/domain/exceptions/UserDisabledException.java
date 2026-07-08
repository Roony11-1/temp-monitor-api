package io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class UserDisabledException extends AppException 
{
    public UserDisabledException() 
    {
        super("AUTH-002", "Usuario desactivado", StandardErrorCategories.ACCESS_DENIED, "Usuario desactivado. Contacte al administrador.");
    }
}
