package io.github.roony11_1.temp_monitor.modules.camara.core.application.compaction;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.GranularidadLectura;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Handler para {@code lecturas} / {@code lecturas_resumen}.
 * OCP + SRP: encapsula SQL específico de esta tabla. Usa {@link GranularidadLectura#getDateTrunc()}
 * en lugar de literals 'day'/'month' dispersos.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LecturaCompactionHandler implements CompactionHandler {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public String name() {
        return "lecturas";
    }

    @Override
    public int rollupDaily(Instant cutoff) {
        String daily = GranularidadLectura.DAILY.name();
        String truncDay = GranularidadLectura.DAILY.getDateTrunc();
        String sql = """
                INSERT INTO lecturas_resumen
                    (sensor_uuid, granularidad, bucket_start, promedio, minimo, maximo, conteo, actualizado_en)
                SELECT l.sensor_uuid, '%s', date_trunc('%s', l.timestamp),
                       AVG(l.temperatura), MIN(l.temperatura), MAX(l.temperatura), COUNT(*), now()
                FROM lecturas l
                WHERE l.timestamp < ?
                  AND NOT EXISTS (
                      SELECT 1 FROM lecturas_resumen r
                      WHERE r.sensor_uuid = l.sensor_uuid
                        AND r.granularidad = '%s'
                        AND r.bucket_start = date_trunc('%s', l.timestamp)
                  )
                GROUP BY l.sensor_uuid, date_trunc('%s', l.timestamp)
                ON CONFLICT (sensor_uuid, granularidad, bucket_start) DO NOTHING
                """.formatted(daily, truncDay, daily, truncDay, truncDay);

        Integer insertados = jdbcTemplate.update(sql, cutoff);
        if (insertados != null && insertados > 0) {
            log.info("Rollup diario (lecturas): {} buckets", insertados);
        }
        return insertados != null ? insertados : 0;
    }

    @Override
    public int rollupMonthly(Instant cutoff) {
        String daily = GranularidadLectura.DAILY.name();
        String monthly = GranularidadLectura.MONTHLY.name();
        String truncMonth = GranularidadLectura.MONTHLY.getDateTrunc();
        String sqlInsert = """
                INSERT INTO lecturas_resumen
                    (sensor_uuid, granularidad, bucket_start, promedio, minimo, maximo, conteo, actualizado_en)
                SELECT r.sensor_uuid, '%s', date_trunc('%s', r.bucket_start),
                       AVG(r.promedio), MIN(r.minimo), MAX(r.maximo), SUM(r.conteo), now()
                FROM lecturas_resumen r
                WHERE r.granularidad = '%s'
                  AND r.bucket_start < ?
                  AND NOT EXISTS (
                      SELECT 1 FROM lecturas_resumen m
                      WHERE m.sensor_uuid = r.sensor_uuid
                        AND m.granularidad = '%s'
                        AND m.bucket_start = date_trunc('%s', r.bucket_start)
                  )
                GROUP BY r.sensor_uuid, date_trunc('%s', r.bucket_start)
                ON CONFLICT (sensor_uuid, granularidad, bucket_start) DO NOTHING
                """.formatted(monthly, truncMonth, daily, monthly, truncMonth, truncMonth);

        Integer insertados = jdbcTemplate.update(sqlInsert, cutoff);
        if (insertados != null && insertados > 0) {
            String sqlDelete = """
                    DELETE FROM lecturas_resumen r
                    USING lecturas_resumen m
                    WHERE r.granularidad = '%s'
                      AND r.bucket_start < ?
                      AND m.sensor_uuid = r.sensor_uuid
                      AND m.granularidad = '%s'
                      AND m.bucket_start = date_trunc('%s', r.bucket_start)
                    """.formatted(daily, monthly, truncMonth);
            jdbcTemplate.update(sqlDelete, cutoff);
        }
        return insertados != null ? insertados : 0;
    }

    @Override
    public int purgeRaw(Instant cutoff, int maxBatch) {
        int total = 0;
        int borrados;
        do {
            borrados = jdbcTemplate.update("""
                    DELETE FROM lecturas
                    WHERE id IN (
                        SELECT id FROM lecturas WHERE timestamp < ? LIMIT ?
                    )
                    """, cutoff, maxBatch);
            total += borrados;
        } while (borrados > 0);
        if (total > 0) {
            log.info("Purga de lecturas: {} filas", total);
        }
        return total;
    }
}
