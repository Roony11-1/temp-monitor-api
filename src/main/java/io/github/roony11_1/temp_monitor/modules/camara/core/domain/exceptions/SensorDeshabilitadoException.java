package io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions;

import io.github.roony11_1.error.core.exceptions.AppException;

public class SensorDeshabilitadoException extends AppException 
{
    public SensorDeshabilitadoException(String uuid) 
    {
        super("SENSOR-003", "El sensor está deshabilitado: " + uuid, SensorErrorCategories.SENSOR_DESHABILITADO, "El sensor está deshabilitado: " + uuid);
    }
}
