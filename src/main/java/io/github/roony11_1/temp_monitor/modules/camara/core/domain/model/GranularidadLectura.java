package io.github.roony11_1.temp_monitor.modules.camara.core.domain.model;

/**
 * Nivel de agregación de las lecturas compactadas (rollup).
 * {@code DAILY}: un bucket por sensor y día; {@code MONTHLY}: por sensor y mes.
 */
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GranularidadLectura
{
    DAILY("day"),
    MONTHLY("month");

    private final String dateTrunc;

    /**
     * Función SQL para {@code date_trunc} correspondiente a la granularidad.
     * OCP: agregar {@code HOURLY("hour")} no requiere tocar SQL hardcodeado.
     */
    public String getDateTruncSql() {
        return dateTrunc;
    }
}