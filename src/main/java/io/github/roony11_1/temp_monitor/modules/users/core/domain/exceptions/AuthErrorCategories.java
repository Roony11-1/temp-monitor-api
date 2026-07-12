package io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions;

import io.github.roony11_1.error.core.ErrorCategory;

public final class AuthErrorCategories 
{
    private AuthErrorCategories() {}

    public static final ErrorCategory INVALID_CREDENTIALS = new ErrorCategory() 
    {
        @Override public String name() { return "INVALID_CREDENTIALS"; }
        @Override public String description() { return "Credenciales de autenticación inválidas"; }
    };
}
