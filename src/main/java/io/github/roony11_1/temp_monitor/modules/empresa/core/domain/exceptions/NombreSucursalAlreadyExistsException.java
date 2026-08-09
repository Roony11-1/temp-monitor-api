package io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class NombreSucursalAlreadyExistsException extends AppException 
{
    public NombreSucursalAlreadyExistsException(String nombre) 
    {
        super("EMP-002", "El nombre de sucursal ya existe: " + nombre, StandardErrorCategories.ALREADY_EXISTS, "El nombre de sucursal ya existe: " + nombre);
    }
}

