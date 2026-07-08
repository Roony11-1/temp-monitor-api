package io.github.roony11_1.temp_monitor.kernel.security.error;

import io.github.roony11_1.error.core.ErrorCategory;

public final class SecurityErrorCategories 
{
    private SecurityErrorCategories() {}

    public static final ErrorCategory JWT_GENERATION_FAILED = new ErrorCategory() 
    {
        @Override public String name() { return "JWT_GENERATION_FAILED"; }
        @Override public String description() { return "Fallo al generar el token JWT"; }
    };

    public static final ErrorCategory INVALID_TOKEN_USER = new ErrorCategory() 
    {
        @Override public String name() { return "INVALID_TOKEN_USER"; }
        @Override public String description() { return "El usuario del token no es válido"; }
    };
}
