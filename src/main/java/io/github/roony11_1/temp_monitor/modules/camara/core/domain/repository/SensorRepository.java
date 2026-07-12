package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, Long>
{
    Optional<Sensor> findByUuid(UUID uuid);

    Optional<Sensor> findByMacAddress(String macAddress);

    Optional<Sensor> findByApiKeyHash(String apiKeyHash);

    List<Sensor> findByCamaraId(Long camaraId);

    @Query("SELECT s FROM Sensor s LEFT JOIN FETCH s.camara c LEFT JOIN FETCH c.sucursal sc LEFT JOIN FETCH sc.empresa")
    List<Sensor> findAllWithHierarchy();

    @Query("SELECT s FROM Sensor s LEFT JOIN FETCH s.camara c LEFT JOIN FETCH c.sucursal sc LEFT JOIN FETCH sc.empresa WHERE s.uuid = :uuid")
    Optional<Sensor> findByUuidWithHierarchy(UUID uuid);

    @Query("SELECT s FROM Sensor s LEFT JOIN FETCH s.camara c LEFT JOIN FETCH c.sucursal sc LEFT JOIN FETCH sc.empresa WHERE c.id = :camaraId")
    List<Sensor> findByCamaraIdWithHierarchy(Long camaraId);
}
