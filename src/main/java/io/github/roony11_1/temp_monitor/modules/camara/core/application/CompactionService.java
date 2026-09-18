package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import io.github.roony11_1.temp_monitor.config.CompactionConfig;
import io.github.roony11_1.temp_monitor.modules.camara.core.application.compaction.CompactionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Compactación (rollup piramidal) y purga de {@code lecturas}.
 *
 * <p>OCP: delega a {@link CompactionHandler}s inyectados. Agregar nueva fuente
 * (ej. {@code dispositivo_lecturas}) solo requiere un nuevo bean que implemente
 * {@code CompactionHandler}, sin modificar esta clase.
 *
 * <p>Usa {@link io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.GranularidadLectura}
 * con {@code getDateTrunc()} en lugar de literals 'day'/'month' dispersos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompactionService 
{
    private final CompactionConfig config;
    private final JdbcTemplate jdbcTemplate;
    private final List<CompactionHandler> handlers;

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

        Instant cutoffDias = Instant.now().minus(config.getRetencionDias(), ChronoUnit.DAYS);
        Instant cutoffMeses = ZonedDateTime.now(ZoneOffset.UTC).minusMonths(config.getRetencionMeses()).toInstant();

        int totalDiarios = 0, totalMensuales = 0, totalPurgados = 0;
        for (CompactionHandler handler : handlers) {
            int diarios = handler.rollupDaily(cutoffDias);
            int mensuales = handler.rollupMonthly(cutoffMeses);
            int purgados = handler.purgeRaw(cutoffDias, config.getMaxLote());
            totalDiarios += diarios;
            totalMensuales += mensuales;
            totalPurgados += purgados;
            log.info("Handler '{}': {} diarios, {} mensuales, {} purgados", handler.name(), diarios, mensuales, purgados);
        }

        log.info("Compactación finalizada: {} buckets diarios totales, {} mensuales totales, {} filas crudas purgadas totales",
                totalDiarios, totalMensuales, totalPurgados);
    }
}
