package io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class EmpresaNotFoundException extends AppException {

    public EmpresaNotFoundException(String detail) {
        super("EMP-001", "Empresa no encontrada: " + detail, StandardErrorCategories.NOT_FOUND, "Empresa no encontrada: " + detail);
    }
}
