package io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions;

import io.github.roony11_1.error.core.ErrorCategory;

public final class SensorErrorCategories 
{
    private SensorErrorCategories() {}

    public static final ErrorCategory SENSOR_NOT_FOUND = new ErrorCategory() 
    {
        @Override public String name() { return "SENSOR_NOT_FOUND"; }
        @Override public String description() { return "Sensor no encontrado"; }
    };

    public static final ErrorCategory SENSOR_ALREADY_EXISTS = new ErrorCategory() 
    {
        @Override public String name() { return "SENSOR_ALREADY_EXISTS"; }
        @Override public String description() { return "El sensor ya está registrado"; }
    };

    public static final ErrorCategory SENSOR_DESHABILITADO = new ErrorCategory() 
    {
        @Override public String name() { return "SENSOR_DESHABILITADO"; }
        @Override public String description() { return "El sensor está deshabilitado"; }
    };

    public static final ErrorCategory SENSOR_SIN_CAMARA = new ErrorCategory() 
    {
        @Override public String name() { return "SENSOR_SIN_CAMARA"; }
        @Override public String description() { return "El sensor no tiene una cámara asignada"; }
    };

    public static final ErrorCategory SENSOR_API_KEY_INVALIDA = new ErrorCategory() 
    {
        @Override public String name() { return "SENSOR_API_KEY_INVALIDA"; }
        @Override public String description() { return "API Key del sensor inválida"; }
    };
}
