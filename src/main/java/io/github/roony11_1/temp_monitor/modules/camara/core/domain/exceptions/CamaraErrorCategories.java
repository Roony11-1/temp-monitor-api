package io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions;

import io.github.roony11_1.error.core.ErrorCategory;

public final class CamaraErrorCategories 
{
    private CamaraErrorCategories() {}

    public static final ErrorCategory CAMARA_NOT_FOUND = new ErrorCategory() 
    {
        @Override public String name() { return "CAMARA_NOT_FOUND"; }
        @Override public String description() { return "Cámara no encontrada"; }
    };

    public static final ErrorCategory RANGO_TEMPERATURA_INVALIDO = new ErrorCategory() 
    {
        @Override public String name() { return "RANGO_TEMPERATURA_INVALIDO"; }
        @Override public String description() { return "Rango de temperatura inválido"; }
    };
}
