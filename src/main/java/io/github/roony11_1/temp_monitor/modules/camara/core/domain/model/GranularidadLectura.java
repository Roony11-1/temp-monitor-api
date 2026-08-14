package io.github.roony11_1.temp_monitor.modules.camara.core.domain.model;

/**
 * Nivel de agregación de las lecturas compactadas (rollup).
 * {@code DAILY}: un bucket por sensor y día; {@code MONTHLY}: por sensor y mes.
 */
public enum GranularidadLectura
{
    DAILY,
    MONTHLY
}