package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Lectura;

public interface LecturaRepository extends JpaRepository<Lectura, Long>
{
    List<Lectura> findBySensorUuidOrderByTimestampDesc(UUID sensorUuid);
}
