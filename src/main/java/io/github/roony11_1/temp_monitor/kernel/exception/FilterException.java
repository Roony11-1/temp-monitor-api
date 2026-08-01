package io.github.roony11_1.temp_monitor.kernel.exception;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class FilterException extends AppException 
{
    public FilterException(String detail) 
    {
        super("FIL-001", "Error en filtro: " + detail, StandardErrorCategories.INVALID_INPUT, "Error en filtro: " + detail);
    }

    public FilterException(String detail, Throwable cause) 
    {
        super("FIL-001", "Error en filtro: " + detail, StandardErrorCategories.INVALID_INPUT, "Error en filtro: " + detail);
        initCause(cause);
    }
}
