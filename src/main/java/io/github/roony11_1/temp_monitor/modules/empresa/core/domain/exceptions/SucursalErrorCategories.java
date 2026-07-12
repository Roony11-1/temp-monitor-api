package io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions;

import io.github.roony11_1.error.core.ErrorCategory;

public final class SucursalErrorCategories 
{
    private SucursalErrorCategories() {}

    public static final ErrorCategory SUCURSAL_NOT_FOUND = new ErrorCategory() 
    {
        @Override public String name() { return "SUCURSAL_NOT_FOUND"; }
        @Override public String description() { return "Sucursal no encontrada"; }
    };

    public static final ErrorCategory SUCURSAL_ALREADY_EXISTS = new ErrorCategory() 
    {
        @Override public String name() { return "SUCURSAL_ALREADY_EXISTS"; }
        @Override public String description() { return "La sucursal ya existe"; }
    };
}
