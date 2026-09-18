package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import io.github.roony11_1.temp_monitor.config.CompactionConfig;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.modules.camara.core.application.compaction.CompactionHandler;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.CompactionJob;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoCompactionJob;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CompactionJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompactionJobService {

    private final CompactionJobRepository jobRepository;
    private final CompactionConfig config;
    private final JdbcTemplate jdbcTemplate;
    private final List<CompactionHandler> handlers;

    @Transactional
    public CompactionJob crearJob(TokenUser ejecutadoPor) {
        CompactionJob job = CompactionJob.builder()
                .id(UUID.randomUUID())
                .estado(EstadoCompactionJob.RUNNING)
                .iniciadoEn(Instant.now())
                .ejecutadoPorId(ejecutadoPor.id())
                .ejecutadoPorEmail(ejecutadoPor.email())
                .build();
        return jobRepository.save(job);
    }

    @Async("compactionExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ejecutarAsync(UUID jobId) {
        Optional<CompactionJob> opt = jobRepository.findById(jobId);
        if (opt.isEmpty()) {
            log.error("Compaction job no encontrado: {}", jobId);
            return;
        }
        CompactionJob job = opt.get();
        try {
            log.info("Compactación async iniciada por job {} (usuario {}), retención {}d/{}m", jobId, job.getEjecutadoPorEmail(), config.getRetencionDias(), config.getRetencionMeses());
            // Asegura UTC como en CompactionService
            jdbcTemplate.execute("SET TIME ZONE 'UTC'");

            Instant cutoffDias = Instant.now().minus(config.getRetencionDias(), ChronoUnit.DAYS);
            Instant cutoffMeses = ZonedDateTime.now(ZoneOffset.UTC).minusMonths(config.getRetencionMeses()).toInstant();

            int totalDiarios = 0, totalMensuales = 0, totalPurgados = 0;
            StringBuilder detalle = new StringBuilder("[");
            StringBuilder errores = new StringBuilder();
            for (int i = 0; i < handlers.size(); i++) {
                CompactionHandler handler = handlers.get(i);
                try {
                    int diarios = handler.rollupDaily(cutoffDias);
                    int mensuales = handler.rollupMonthly(cutoffMeses);
                    int purgados = handler.purgeRaw(cutoffDias, config.getMaxLote());
                    totalDiarios += diarios;
                    totalMensuales += mensuales;
                    totalPurgados += purgados;
                    detalle.append("{\"name\":\"").append(handler.name()).append("\",\"diarios\":").append(diarios)
                            .append(",\"mensuales\":").append(mensuales).append(",\"purgados\":").append(purgados).append("}");
                    if (i < handlers.size() - 1) detalle.append(",");
                    log.info("Handler '{}' job {}: {} diarios, {} mensuales, {} purgados", handler.name(), jobId, diarios, mensuales, purgados);
                } catch (Exception he) {
                    log.error("Handler '{}' job {} falló", handler.name(), jobId, he);
                    if (errores.length() > 0) errores.append(" | ");
                    errores.append(handler.name()).append(": ").append(he.toString());
                    // corta stack para no exceder TEXT
                    Throwable cause = he.getCause();
                    if (cause != null) errores.append(" cause: ").append(cause.toString());
                    detalle.append("{\"name\":\"").append(handler.name()).append("\",\"diarios\":0,\"mensuales\":0,\"purgados\":0,\"error\":\"").append(he.getMessage() != null ? he.getMessage().replace("\"", "'") : "unknown").append("\"}");
                    if (i < handlers.size() - 1) detalle.append(",");
                }
            }
            detalle.append("]");

            job.setTotalDiarios(totalDiarios);
            job.setTotalMensuales(totalMensuales);
            job.setTotalPurgados(totalPurgados);
            job.setDetalleJson(detalle.toString());
            if (errores.length() > 0) {
                job.setEstado(EstadoCompactionJob.FAILED);
                job.setError(errores.toString());
            } else {
                job.setEstado(EstadoCompactionJob.COMPLETED);
            }
            job.setTerminadoEn(Instant.now());
            jobRepository.save(job);
            if (errores.length() > 0) {
                log.warn("Compactación async job {} finalizada con errores parciales: {}", jobId, errores);
            } else {
                log.info("Compactación async job {} finalizada: {} diarios, {} mensuales, {} purgados", jobId, totalDiarios, totalMensuales, totalPurgados);
            }
        } catch (Exception e) {
            log.error("Compactación async job {} falló", jobId, e);
            String fullError = e.toString();
            Throwable c = e.getCause();
            if (c != null) fullError += " | cause: " + c.toString();
            // incluye stack truncado
            String stack = java.util.Arrays.stream(e.getStackTrace()).limit(5).map(StackTraceElement::toString).collect(Collectors.joining(" | "));
            job.setEstado(EstadoCompactionJob.FAILED);
            job.setError(fullError + " | stack: " + stack);
            job.setTerminadoEn(Instant.now());
            try {
                jobRepository.save(job);
            } catch (Exception ex) {
                log.error("No se pudo guardar estado FAILED para job {}", jobId, ex);
            }
        }
    }

    @Transactional(readOnly = true)
    public Optional<CompactionJob> obtener(UUID id) {
        return jobRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<CompactionJob> listarUltimos(int limit) {
        return jobRepository.findAll().stream()
                .sorted((a,b) -> b.getIniciadoEn().compareTo(a.getIniciadoEn()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
