package io.github.roony11_1.temp_monitor.modules.camara.core.application.compaction;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.GranularidadLectura;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class CamaraLecturaCompactionHandler implements CompactionHandler {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public String name() {
        return "camara_lecturas";
    }

    @Override
    public int rollupDaily(Instant cutoff) {
        ensureIndex();
        String daily = GranularidadLectura.DAILY.name();
        String truncDay = GranularidadLectura.DAILY.getDateTrunc();
        String sql = """
                INSERT INTO camara_lecturas_resumen
                    (camara_id, granularidad, bucket_start, promedio, minimo, maximo, conteo, actualizado_en)
                SELECT cl.camara_id, '%s', date_trunc('%s', cl.bucket_start),
                       AVG(cl.promedio), MIN(cl.promedio), MAX(cl.promedio), COUNT(*)::int, now()
                FROM camara_lecturas cl
                WHERE cl.bucket_start < ?
                  AND NOT EXISTS (
                      SELECT 1 FROM camara_lecturas_resumen r
                      WHERE r.camara_id = cl.camara_id
                        AND r.granularidad = '%s'
                        AND r.bucket_start = date_trunc('%s', cl.bucket_start)
                  )
                GROUP BY cl.camara_id, date_trunc('%s', cl.bucket_start)
                ON CONFLICT ON CONSTRAINT uk_camara_lecturas_resumen_bucket DO NOTHING
                """.formatted(daily, truncDay, daily, truncDay, truncDay);

        Integer insertados = jdbcTemplate.update(sql, Timestamp.from(cutoff));
        if (insertados != null && insertados > 0) {
            log.info("Rollup diario de cámaras: {} buckets", insertados);
        }
        return insertados != null ? insertados : 0;
    }

    @Override
    public int rollupMonthly(Instant cutoff) {
        ensureIndex();
        String daily = GranularidadLectura.DAILY.name();
        String monthly = GranularidadLectura.MONTHLY.name();
        String truncMonth = GranularidadLectura.MONTHLY.getDateTrunc();
        String sqlInsert = """
                INSERT INTO camara_lecturas_resumen
                    (camara_id, granularidad, bucket_start, promedio, minimo, maximo, conteo, actualizado_en)
                SELECT r.camara_id, '%s', date_trunc('%s', r.bucket_start),
                       AVG(r.promedio), MIN(r.minimo), MAX(r.maximo), SUM(r.conteo), now()
                FROM camara_lecturas_resumen r
                WHERE r.granularidad = '%s'
                  AND r.bucket_start < ?
                  AND NOT EXISTS (
                      SELECT 1 FROM camara_lecturas_resumen m
                      WHERE m.camara_id = r.camara_id
                        AND m.granularidad = '%s'
                        AND m.bucket_start = date_trunc('%s', r.bucket_start)
                  )
                GROUP BY r.camara_id, date_trunc('%s', r.bucket_start)
                ON CONFLICT ON CONSTRAINT uk_camara_lecturas_resumen_bucket DO NOTHING
                """.formatted(monthly, truncMonth, daily, monthly, truncMonth, truncMonth);

        Integer insertados = jdbcTemplate.update(sqlInsert, Timestamp.from(cutoff));
        if (insertados != null && insertados > 0) {
            String sqlDelete = """
                    DELETE FROM camara_lecturas_resumen r
                    USING camara_lecturas_resumen m
                    WHERE r.granularidad = '%s'
                      AND r.bucket_start < ?
                      AND m.camara_id = r.camara_id
                      AND m.granularidad = '%s'
                      AND m.bucket_start = date_trunc('%s', r.bucket_start)
                    """.formatted(daily, monthly, truncMonth);
            jdbcTemplate.update(sqlDelete, Timestamp.from(cutoff));
        }
        return insertados != null ? insertados : 0;
    }

    private void ensureIndex() {
        try {
            jdbcTemplate.execute("CREATE UNIQUE INDEX IF NOT EXISTS uk_camara_lecturas_resumen_bucket ON camara_lecturas_resumen (camara_id, granularidad, bucket_start)");
        } catch (Exception e) {
            log.warn("No se pudo asegurar índice uk_camara_lecturas_resumen_bucket", e);
        }
    }

    @Override
    public int purgeRaw(Instant cutoff, int maxBatch) {
        int total = 0;
        int borrados;
        do {
            borrados = jdbcTemplate.update("""
                    DELETE FROM camara_lecturas
                    WHERE id IN (
                        SELECT id FROM camara_lecturas WHERE bucket_start < ? LIMIT ?
                    )
                    """, Timestamp.from(cutoff), maxBatch);
            total += borrados;
        } while (borrados > 0);
        if (total > 0) {
            log.info("Purga de camara_lecturas: {} filas", total);
        }
        return total;
    }
}
