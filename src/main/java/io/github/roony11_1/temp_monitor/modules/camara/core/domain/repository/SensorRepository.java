package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, Long>, JpaSpecificationExecutor<Sensor>
{
    Optional<Sensor> findByUuid(UUID uuid);

    Optional<Sensor> findByMacAddress(String macAddress);

    Optional<Sensor> findByApiKeyHash(String apiKeyHash);

    List<Sensor> findByCamaraId(Long camaraId);

    @Query(value = "SELECT s FROM Sensor s LEFT JOIN FETCH s.camara c LEFT JOIN FETCH c.sucursal sc LEFT JOIN FETCH sc.empresa",
           countQuery = "SELECT COUNT(s) FROM Sensor s")
    List<Sensor> findAllWithHierarchy();

    @Query(value = "SELECT s FROM Sensor s LEFT JOIN FETCH s.camara c LEFT JOIN FETCH c.sucursal sc LEFT JOIN FETCH sc.empresa",
           countQuery = "SELECT COUNT(s) FROM Sensor s")
    Page<Sensor> findAllWithHierarchy(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"camara", "camara.sucursal", "camara.sucursal.empresa"})
    Page<Sensor> findAll(Specification<Sensor> spec, Pageable pageable);

    @Query("SELECT s FROM Sensor s LEFT JOIN FETCH s.camara c LEFT JOIN FETCH c.sucursal sc LEFT JOIN FETCH sc.empresa WHERE s.uuid = :uuid")
    Optional<Sensor> findByUuidWithHierarchy(UUID uuid);

    @Query(value = "SELECT s FROM Sensor s LEFT JOIN FETCH s.camara c LEFT JOIN FETCH c.sucursal sc LEFT JOIN FETCH sc.empresa WHERE c.id = :camaraId",
           countQuery = "SELECT COUNT(s) FROM Sensor s WHERE s.camara.id = :camaraId")
    List<Sensor> findByCamaraIdWithHierarchy(Long camaraId);

    @Query(value = "SELECT s FROM Sensor s LEFT JOIN FETCH s.camara c LEFT JOIN FETCH c.sucursal sc LEFT JOIN FETCH sc.empresa WHERE c.id = :camaraId",
           countQuery = "SELECT COUNT(s) FROM Sensor s WHERE s.camara.id = :camaraId")
    Page<Sensor> findByCamaraIdWithHierarchy(@Param("camaraId") Long camaraId, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Sensor s WHERE s.estado = io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor.ACTIVO AND s.ultimoContacto > :threshold")
    long countOnline(@Param("threshold") Instant threshold);

    @Query("SELECT COUNT(s) FROM Sensor s WHERE s.estado = io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor.ACTIVO AND (s.ultimoContacto IS NULL OR s.ultimoContacto <= :threshold)")
    long countOffline(@Param("threshold") Instant threshold);

    long countByEstado(io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor estado);
}
