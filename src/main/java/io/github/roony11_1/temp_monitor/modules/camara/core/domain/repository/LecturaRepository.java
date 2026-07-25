package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Lectura;

public interface LecturaRepository extends JpaRepository<Lectura, Long>
{
    List<Lectura> findBySensorUuidOrderByTimestampDesc(UUID sensorUuid);

    Page<Lectura> findBySensorUuidOrderByTimestampDesc(UUID sensorUuid, Pageable pageable);

    @Query("SELECT l FROM Lectura l WHERE l.timestamp > :since ORDER BY l.timestamp ASC")
    List<Lectura> findUltimas24h(@Param("since") Instant since);
}
