package io.github.roony11_1.temp_monitor.modules.camara.core.domain.model;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorDeshabilitadoException;

public enum EstadoSensor 
{
    PENDIENTE {
        @Override public boolean canRecord() { return false; }
        @Override public boolean canAuthenticate() { return true; }
        @Override public boolean canBeAssignedManually() { return false; }
    },
    ACTIVO {
        @Override public boolean canRecord() { return true; }
        @Override public boolean canAuthenticate() { return true; }
        @Override public boolean canBeAssignedManually() { return true; }
    },
    DESHABILITADO {
        @Override public boolean canRecord() { return false; }
        @Override public boolean canAuthenticate() { return false; }
        @Override public boolean canBeAssignedManually() { return true; }
    };

    /**
     * Indica si un sensor en este estado puede registrar lecturas de temperatura.
     * Solo {@link #ACTIVO} puede; {@code PENDIENTE} y {@code DESHABILITADO} no.
     */
    public abstract boolean canRecord();

    /**
     * Indica si el sensor puede autenticarse vía API Key.
     * {@code PENDIENTE} y {@code ACTIVO} sí; {@code DESHABILITADO} no.
     */
    public abstract boolean canAuthenticate();

    /**
     * Indica si este estado puede ser asignado manualmente vía API.
     * {@code PENDIENTE} es solo estado inicial del sistema y no puede ser asignado manualmente.
     */
    public abstract boolean canBeAssignedManually();

    /**
     * Conveniencia OCP: lanza {@link SensorDeshabilitadoException} si el estado no permite autenticación.
     */
    public void assertCanAuthenticate(String sensorUuid) {
        if (!canAuthenticate()) {
            throw new SensorDeshabilitadoException(sensorUuid);
        }
    }

    public boolean isPending() {
        return this == PENDIENTE;
    }
}
