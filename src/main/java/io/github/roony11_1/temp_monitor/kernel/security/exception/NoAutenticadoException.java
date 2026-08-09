package io.github.roony11_1.temp_monitor.kernel.security.exception;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class NoAutenticadoException extends AppException 
{
    public NoAutenticadoException(String detail) 
    {
        super("AUTH-005", detail, StandardErrorCategories.UNAUTHORIZED, detail);
    }
}