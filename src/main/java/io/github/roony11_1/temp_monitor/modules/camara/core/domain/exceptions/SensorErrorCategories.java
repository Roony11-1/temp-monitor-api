package io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions;

import io.github.roony11_1.error.core.ErrorCategory;

public enum SensorErrorCategories implements ErrorCategory
{
    SENSOR_DESHABILITADO("El sensor está deshabilitado"),
    SENSOR_SIN_CAMARA("El sensor no tiene una cámara asignada");

    private final String description;

    SensorErrorCategories(String description)
    {
        this.description = description;
    }

    @Override
    public String description()
    {
        return description;
    }
}