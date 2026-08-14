package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.GranularidadLectura;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.LecturaResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LecturaResumenRepository extends JpaRepository<LecturaResumen, Long>
{
    Page<LecturaResumen> findBySensorUuidAndGranularidadOrderByBucketStartDesc(
            UUID sensorUuid, GranularidadLectura granularidad, Pageable pageable);

    Optional<LecturaResumen> findBySensorUuidAndGranularidadAndBucketStart(
            UUID sensorUuid, GranularidadLectura granularidad, java.time.Instant bucketStart);
}