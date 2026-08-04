package io.github.roony11_1.temp_monitor.kernel.security.exception;

import io.github.roony11_1.error.core.exceptions.AppException;
import io.github.roony11_1.temp_monitor.kernel.security.error.SecurityErrorCategories;

public class NoAutenticadoException extends AppException 
{
    public NoAutenticadoException(String detail) 
    {
        super("AUTH-005", detail, SecurityErrorCategories.AUTHENTICATION_REQUIRED, detail);
    }
}