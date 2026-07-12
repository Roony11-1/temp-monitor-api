package io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions;

import io.github.roony11_1.error.core.ErrorCategory;

public final class UserErrorCategories 
{
    private UserErrorCategories() {}

    public static final ErrorCategory USER_NOT_FOUND = new ErrorCategory() 
    {
        @Override public String name() { return "USER_NOT_FOUND"; }
        @Override public String description() { return "Usuario no encontrado"; }
    };

    public static final ErrorCategory USER_DISABLED = new ErrorCategory() 
    {
        @Override public String name() { return "USER_DISABLED"; }
        @Override public String description() { return "Usuario desactivado"; }
    };

    public static final ErrorCategory EMAIL_ALREADY_EXISTS = new ErrorCategory() 
    {
        @Override public String name() { return "EMAIL_ALREADY_EXISTS"; }
        @Override public String description() { return "El email ya está registrado"; }
    };
}
