package io.github.roony11_1.temp_monitor.kernel.security.exception;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class AccesoDenegadoException extends AppException 
{
    public AccesoDenegadoException(String detail) 
    {
        super("AUTH-004", detail, StandardErrorCategories.ACCESS_DENIED, detail);
    }
}