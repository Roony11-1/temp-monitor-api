package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, Long>
{
    Optional<Sensor> findByUuid(UUID uuid);

    Optional<Sensor> findByMacAddress(String macAddress);

    Optional<Sensor> findByApiKeyHash(String apiKeyHash);
}
