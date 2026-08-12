package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Lectura;

public interface LecturaRepository extends JpaRepository<Lectura, Long>
{
    Page<Lectura> findBySensorUuidOrderByTimestampDesc(UUID sensorUuid, Pageable pageable);

    Page<Lectura> findBySensorUuidAndTimestampAfterOrderByTimestampDesc(UUID sensorUuid, Instant since, Pageable pageable);

    @Query("SELECT l FROM Lectura l WHERE l.timestamp > :since ORDER BY l.timestamp ASC")
    List<Lectura> findUltimas24h(@Param("since") Instant since);

    @Query("SELECT AVG(l.temperatura) FROM Lectura l WHERE l.timestamp > :since AND l.sensorUuid IN "
        + "(SELECT s.uuid FROM Sensor s WHERE s.camara.id = :camaraId AND s.estado = :estado)")
    Double calcularPromedioPorCamara(@Param("camaraId") Long camaraId, @Param("since") Instant since, @Param("estado") EstadoSensor estado);

    @Query("SELECT COUNT(DISTINCT l.sensorUuid) FROM Lectura l WHERE l.timestamp > :since AND l.sensorUuid IN "
        + "(SELECT s.uuid FROM Sensor s WHERE s.camara.id = :camaraId AND s.estado = :estado)")
    long contarSensoresConDatosPorCamara(@Param("camaraId") Long camaraId, @Param("since") Instant since, @Param("estado") EstadoSensor estado);

    @Query("SELECT MAX(l.timestamp) FROM Lectura l WHERE l.sensorUuid IN "
        + "(SELECT s.uuid FROM Sensor s WHERE s.camara.id = :camaraId)")
    Optional<Instant> findUltimaLecturaPorCamara(@Param("camaraId") Long camaraId);

    @Query("SELECT l FROM Lectura l WHERE l.sensorUuid IN "
        + "(SELECT s.uuid FROM Sensor s WHERE s.camara.id = :camaraId) "
        + "AND l.timestamp = (SELECT MAX(l2.timestamp) FROM Lectura l2 WHERE l2.sensorUuid = l.sensorUuid)")
    List<Lectura> findUltimaPorSensorDeCamara(@Param("camaraId") Long camaraId);
}
