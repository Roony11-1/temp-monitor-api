package io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions;

import io.github.roony11_1.error.core.exceptions.AppException;

public class CamaraNotFoundException extends AppException 
{
    public CamaraNotFoundException(String detail) 
    {
        super("CAM-001", "Cámara no encontrada: " + detail, CamaraErrorCategories.CAMARA_NOT_FOUND, "Cámara no encontrada: " + detail);
    }
}
