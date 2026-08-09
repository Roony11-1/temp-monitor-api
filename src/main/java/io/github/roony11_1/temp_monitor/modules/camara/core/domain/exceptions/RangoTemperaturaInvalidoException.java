package io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class RangoTemperaturaInvalidoException extends AppException 
{
    public RangoTemperaturaInvalidoException() 
    {
        super("CAM-002", "Rango de temperatura inválido", StandardErrorCategories.INVALID_INPUT, "El mínimo debe ser menor que el máximo");
    }
}
