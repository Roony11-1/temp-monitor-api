package io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class SensorNotFoundException extends AppException 
{
    public SensorNotFoundException(String detail) 
    {
        super("SENSOR-002", "Sensor no encontrado: " + detail, StandardErrorCategories.NOT_FOUND, "Sensor no encontrado: " + detail);
    }
}
