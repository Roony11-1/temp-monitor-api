package io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions;

import io.github.roony11_1.error.core.exceptions.AppException;

public class SucursalNotFoundException extends AppException {

    public SucursalNotFoundException(String detail) {
        super("SUC-001", "Sucursal no encontrada: " + detail, SucursalErrorCategories.SUCURSAL_NOT_FOUND, "Sucursal no encontrada: " + detail);
    }
}
