package io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions;

import io.github.roony11_1.error.core.ErrorCategory;

public final class EmpresaErrorCategories 
{
    private EmpresaErrorCategories() {}

    public static final ErrorCategory EMPRESA_NOT_FOUND = new ErrorCategory() 
    {
        @Override public String name() { return "EMPRESA_NOT_FOUND"; }
        @Override public String description() { return "Empresa no encontrada"; }
    };

    public static final ErrorCategory EMPRESA_ALREADY_EXISTS = new ErrorCategory() 
    {
        @Override public String name() { return "EMPRESA_ALREADY_EXISTS"; }
        @Override public String description() { return "El nombre de empresa ya existe"; }
    };
}
