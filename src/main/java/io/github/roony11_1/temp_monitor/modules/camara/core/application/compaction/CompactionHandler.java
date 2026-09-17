package io.github.roony11_1.temp_monitor.modules.camara.core.application.compaction;

import java.time.Instant;

/**
 * OCP: cada origen de datos (lecturas de sensor, muestras de cámara, futuro: dispositivo, etc.)
 * implementa esta interfaz. Agregar una nueva fuente no requiere modificar {@code CompactionService},
 * solo registrar un nuevo bean {@code CompactionHandler}.
 */
public interface CompactionHandler {

    /** Nombre para logging (ej. "lecturas", "camara_lecturas") */
    String name();

    /**
     * Rollup diario: agrega bruto cerrado y más viejo que retention en bucket DAILY.
     * @return buckets insertados
     */
    int rollupDaily(Instant cutoff);

    /**
     * Rollup mensual: agrega DAILY -> MONTHLY y purga DAILY consumidos.
     * @return buckets mensuales insertados
     */
    int rollupMonthly(Instant cutoff);

    /**
     * Purga bruto más viejo que retention en lotes.
     * @return filas borradas
     */
    int purgeRaw(Instant cutoff, int maxBatch);
}
