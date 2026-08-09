package io.github.roony11_1.temp_monitor.kernel.security.error;

import io.github.roony11_1.error.core.ErrorCategory;

public enum SecurityErrorCategories implements ErrorCategory
{
    JWT_GENERATION_FAILED("Fallo al generar el token JWT"),
    INVALID_TOKEN_USER("El usuario del token no es válido");

    private final String description;

    SecurityErrorCategories(String description)
    {
        this.description = description;
    }

    @Override
    public String description()
    {
        return description;
    }
}