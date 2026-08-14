package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import io.github.roony11_1.temp_monitor.config.CompactionConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Compactación (rollup piramidal) y purga de {@code lecturas}.
 *
 * <p>Los agregados viven en {@code lecturas_resumen}: un bucket {@code DAILY} por
 * sensor y día (promedio, mínimo, máximo y conteo) y otro {@code MONTHLY} por sensor
 * y mes, derivado de los DAILY. Se ejecuta por cron (por defecto domingo 03:00 UTC),
 * es idempotente y backfillea el histórico: cada corrida resuelve los buckets cerrados
 * que aún no existen.
 *
 * <p>El bruto menor a {@code retencion-dias} se conserva sin tocar (detail reciente);
 * el resto se agrega y se purga. La sesión se fija a UTC para que los buckets usen
 * límites de día/mes consistentes con el resto del sistema.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompactionService 
{
    private final CompactionConfig config;
    private final JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "${app.compactacion.cron:0 0 3 * * SUN}")
    @Transactional
    public void compactar() 
    {
        if (!config.isEnabled()) 
        {
            return;
        }

        log.info("Compactación de lecturas iniciada (retención {}d / {} meses)", 
                config.getRetencionDias(), config.getRetencionMeses());

        jdbcTemplate.execute("SET TIME ZONE 'UTC'");

        int diarios = rollupDiario();
        int mensuales = rollupMensual();
        int purgados = purgarBruto();
        int diariosCamara = rollupCamaraDiario();
        int mensualesCamara = rollupCamaraMensual();
        int purgadosCamara = purgarCamaraBruto();

        log.info("Compactación finalizada: {} buckets diarios, {} mensuales, {} filas crudas purgadas; "
                + "{} buckets diarios de cámara, {} mensuales, {} muestras purgadas",
                diarios, mensuales, purgados, diariosCamara, mensualesCamara, purgadosCamara);
    }

    /**
     * Agrega las lecturas crudas cerradas y más viejas que la retención en buckets
     * {@code DAILY} aún no resumidos. @return filas insertadas.
     */
    private int rollupDiario() 
    {
        Instant cutoff = Instant.now().minus(config.getRetencionDias(), ChronoUnit.DAYS);

        Integer insertados = jdbcTemplate.update("""
                INSERT INTO lecturas_resumen
                    (sensor_uuid, granularidad, bucket_start, promedio, minimo, maximo, conteo, actualizado_en)
                SELECT l.sensor_uuid, 'DAILY', date_trunc('day', l.timestamp),
                       AVG(l.temperatura), MIN(l.temperatura), MAX(l.temperatura), COUNT(*), now()
                FROM lecturas l
                WHERE l.timestamp < ?
                  AND NOT EXISTS (
                      SELECT 1 FROM lecturas_resumen r
                      WHERE r.sensor_uuid = l.sensor_uuid
                        AND r.granularidad = 'DAILY'
                        AND r.bucket_start = date_trunc('day', l.timestamp)
                  )
                GROUP BY l.sensor_uuid, date_trunc('day', l.timestamp)
                ON CONFLICT (sensor_uuid, granularidad, bucket_start) DO NOTHING
                """, cutoff);

        if (insertados != null && insertados > 0) 
        {
            log.info("Rollup diario: {} buckets", insertados);
        }
        return insertados != null ? insertados : 0;
    }

    /**
     * Agrega buckets {@code DAILY} más viejos que la retención mensual en {@code MONTHLY}
     * y borra los DAILY consumidos. @return buckets mensuales insertados.
     */
    private int rollupMensual() 
    {
        Instant cutoff = Instant.now().minus(config.getRetencionMeses(), ChronoUnit.MONTHS);

        Integer insertados = jdbcTemplate.update("""
                INSERT INTO lecturas_resumen
                    (sensor_uuid, granularidad, bucket_start, promedio, minimo, maximo, conteo, actualizado_en)
                SELECT r.sensor_uuid, 'MONTHLY', date_trunc('month', r.bucket_start),
                       AVG(r.promedio), MIN(r.minimo), MAX(r.maximo), SUM(r.conteo), now()
                FROM lecturas_resumen r
                WHERE r.granularidad = 'DAILY'
                  AND r.bucket_start < ?
                  AND NOT EXISTS (
                      SELECT 1 FROM lecturas_resumen m
                      WHERE m.sensor_uuid = r.sensor_uuid
                        AND m.granularidad = 'MONTHLY'
                        AND m.bucket_start = date_trunc('month', r.bucket_start)
                  )
                GROUP BY r.sensor_uuid, date_trunc('month', r.bucket_start)
                ON CONFLICT (sensor_uuid, granularidad, bucket_start) DO NOTHING
                """, cutoff);

        if (insertados != null && insertados > 0) 
        {
            jdbcTemplate.update("""
                    DELETE FROM lecturas_resumen r
                    USING lecturas_resumen m
                    WHERE r.granularidad = 'DAILY'
                      AND r.bucket_start < ?
                      AND m.sensor_uuid = r.sensor_uuid
                      AND m.granularidad = 'MONTHLY'
                      AND m.bucket_start = date_trunc('month', r.bucket_start)
                    """, cutoff);
        }

        return insertados != null ? insertados : 0;
    }

    /**
     * Purga el bruto más viejo que la retención en lotes acotados. @return filas borradas.
     */
    private int purgarBruto() 
    {
        Instant cutoff = Instant.now().minus(config.getRetencionDias(), ChronoUnit.DAYS);
        int total = 0;
        int borrados;

        do 
        {
            borrados = jdbcTemplate.update("""
                    DELETE FROM lecturas
                    WHERE id IN (
                        SELECT id FROM lecturas WHERE timestamp < ? LIMIT ?
                    )
                    """, cutoff, config.getMaxLote());
            total += borrados;
        } 
        while (borrados > 0);

        if (total > 0) 
        {
            log.info("Purga de lecturas: {} filas", total);
        }
        return total;
    }

    /**
     * Agrega las muestras de cámara cerradas y más viejas que la retención en buckets
     * {@code DAILY} aún no resumidos. @return filas insertadas.
     */
    private int rollupCamaraDiario() 
    {
        Instant cutoff = Instant.now().minus(config.getRetencionDias(), ChronoUnit.DAYS);

        Integer insertados = jdbcTemplate.update("""
                INSERT INTO camara_lecturas_resumen
                    (camara_id, granularidad, bucket_start, promedio, minimo, maximo, conteo, actualizado_en)
                SELECT cl.camara_id, 'DAILY', date_trunc('day', cl.bucket_start),
                       AVG(cl.promedio), MIN(cl.promedio), MAX(cl.promedio), COUNT(*), now()
                FROM camara_lecturas cl
                WHERE cl.bucket_start < ?
                  AND NOT EXISTS (
                      SELECT 1 FROM camara_lecturas_resumen r
                      WHERE r.camara_id = cl.camara_id
                        AND r.granularidad = 'DAILY'
                        AND r.bucket_start = date_trunc('day', cl.bucket_start)
                  )
                GROUP BY cl.camara_id, date_trunc('day', cl.bucket_start)
                ON CONFLICT (camara_id, granularidad, bucket_start) DO NOTHING
                """, cutoff);

        if (insertados != null && insertados > 0) 
        {
            log.info("Rollup diario de cámaras: {} buckets", insertados);
        }
        return insertados != null ? insertados : 0;
    }

    /**
     * Agrega buckets {@code DAILY} de cámara más viejos que la retención mensual en
     * {@code MONTHLY} y borra los DAILY consumidos. @return buckets mensuales insertados.
     */
    private int rollupCamaraMensual() 
    {
        Instant cutoff = Instant.now().minus(config.getRetencionMeses(), ChronoUnit.MONTHS);

        Integer insertados = jdbcTemplate.update("""
                INSERT INTO camara_lecturas_resumen
                    (camara_id, granularidad, bucket_start, promedio, minimo, maximo, conteo, actualizado_en)
                SELECT r.camara_id, 'MONTHLY', date_trunc('month', r.bucket_start),
                       AVG(r.promedio), MIN(r.minimo), MAX(r.maximo), SUM(r.conteo), now()
                FROM camara_lecturas_resumen r
                WHERE r.granularidad = 'DAILY'
                  AND r.bucket_start < ?
                  AND NOT EXISTS (
                      SELECT 1 FROM camara_lecturas_resumen m
                      WHERE m.camara_id = r.camara_id
                        AND m.granularidad = 'MONTHLY'
                        AND m.bucket_start = date_trunc('month', r.bucket_start)
                  )
                GROUP BY r.camara_id, date_trunc('month', r.bucket_start)
                ON CONFLICT (camara_id, granularidad, bucket_start) DO NOTHING
                """, cutoff);

        if (insertados != null && insertados > 0) 
        {
            jdbcTemplate.update("""
                    DELETE FROM camara_lecturas_resumen r
                    USING camara_lecturas_resumen m
                    WHERE r.granularidad = 'DAILY'
                      AND r.bucket_start < ?
                      AND m.camara_id = r.camara_id
                      AND m.granularidad = 'MONTHLY'
                      AND m.bucket_start = date_trunc('month', r.bucket_start)
                    """, cutoff);
        }

        return insertados != null ? insertados : 0;
    }

    /**
     * Purga las muestras de cámara más viejas que la retención en lotes acotados.
     * @return filas borradas.
     */
    private int purgarCamaraBruto() 
    {
        Instant cutoff = Instant.now().minus(config.getRetencionDias(), ChronoUnit.DAYS);
        int total = 0;
        int borrados;

        do 
        {
            borrados = jdbcTemplate.update("""
                    DELETE FROM camara_lecturas
                    WHERE id IN (
                        SELECT id FROM camara_lecturas WHERE bucket_start < ? LIMIT ?
                    )
                    """, cutoff, config.getMaxLote());
            total += borrados;
        } 
        while (borrados > 0);

        if (total > 0) 
        {
            log.info("Purga de camara_lecturas: {} filas", total);
        }
        return total;
    }
}