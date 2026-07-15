package io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions;

import io.github.roony11_1.error.core.exceptions.AppException;

public class ApiKeyInvalidaException extends AppException 
{
    public ApiKeyInvalidaException() 
    {
        super("SENSOR-004", "API Key inválida", SensorErrorCategories.SENSOR_API_KEY_INVALIDA, "API Key inválida");
    }

    public ApiKeyInvalidaException(String mensaje) 
    {
        super("SENSOR-004", "API Key inválida", SensorErrorCategories.SENSOR_API_KEY_INVALIDA, mensaje);
    }
}
