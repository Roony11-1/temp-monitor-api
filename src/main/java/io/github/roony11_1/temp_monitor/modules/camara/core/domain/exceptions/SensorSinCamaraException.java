package io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions;

import io.github.roony11_1.error.core.exceptions.AppException;

public class SensorSinCamaraException extends AppException 
{
    public SensorSinCamaraException(String uuid) 
    {
        super("SENSOR-004", "El sensor no tiene una cámara asignada: " + uuid, SensorErrorCategories.SENSOR_SIN_CAMARA, "El sensor no tiene una cámara asignada: " + uuid);
    }
}
