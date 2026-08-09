package io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class SensorAlreadyExistsException extends AppException
{
    public SensorAlreadyExistsException(String macAddress) 
    {
        super("SENSOR-001", "El sensor ya está registrado: " + macAddress, StandardErrorCategories.ALREADY_EXISTS, "El sensor ya está registrado: " + macAddress);
    }
}