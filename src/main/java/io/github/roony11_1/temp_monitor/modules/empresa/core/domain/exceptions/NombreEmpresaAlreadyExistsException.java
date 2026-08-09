package io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class NombreEmpresaAlreadyExistsException extends AppException 
{
    public NombreEmpresaAlreadyExistsException(String nombre) 
    {
        super("EMP-002", "El nombre de empresa ya existe: " + nombre, StandardErrorCategories.ALREADY_EXISTS, "El nombre de empresa ya existe: " + nombre);
    }
}
