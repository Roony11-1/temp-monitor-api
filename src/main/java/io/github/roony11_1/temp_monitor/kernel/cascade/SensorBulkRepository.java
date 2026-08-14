package io.github.roony11_1.temp_monitor.kernel.cascade;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

/**
 * Bulk de estado para {@link Sensor} usado por {@link CascadeStateService}.
 * Los repositorios de sensor deben extenderla para heredar estas operaciones.
 *
 * <p>Los updates de estado distinguen el origen del {@code DESHABILITADO} mediante
 * el campo {@code estadoPrevio} del {@link Sensor}:
 * <ul>
 *   <li>{@code bulkDeshabilitarPor*}: solo sensores que aún no están deshabilitados pasan a
 *       {@code DESHABILITADO} y guardan su estado anterior (un deshabilitado a propósito queda
 *       intacto y sin {@code estadoPrevio}).</li>
 *   <li>{@code bulkActivarPor*}: restauran a su {@code estadoPrevio} únicamente los sensores
 *       deshabilitados por la cascada ({@code estadoPrevio IS NOT NULL}); los deshabilitados
 *       manualmente ({@code estadoPrevio IS NULL}) permanecen {@code DESHABILITADO}.</li>
 * </ul>
 *
 * <p>Los {@code WHERE ... s.camara.*} excluyen sensores sin cámara.
 */
@NoRepositoryBean
public interface SensorBulkRepository extends JpaRepository<Sensor, Long>
{
    @Modifying
    @Query("UPDATE Sensor s SET s.deletedAt = :deletedAt WHERE s.camara.id = :camaraId")
    int bulkActualizarDeletedAtPorCamara(@Param("camaraId") Long camaraId, @Param("deletedAt") Instant deletedAt);

    @Modifying
    @Query("UPDATE Sensor s SET s.deletedAt = :deletedAt WHERE s.camara.sucursal.id = :sucursalId")
    int bulkActualizarDeletedAtPorSucursal(@Param("sucursalId") Long sucursalId, @Param("deletedAt") Instant deletedAt);

    @Modifying
    @Query("UPDATE Sensor s SET s.deletedAt = :deletedAt WHERE s.camara.sucursal.empresa.id = :empresaId")
    int bulkActualizarDeletedAtPorEmpresa(@Param("empresaId") Long empresaId, @Param("deletedAt") Instant deletedAt);

    @Modifying
    @Query("UPDATE Sensor s SET s.estado = io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor.DESHABILITADO, s.estadoPrevio = s.estado " +
            "WHERE s.camara.id = :camaraId AND s.estado <> io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor.DESHABILITADO")
    int bulkDeshabilitarPorCamara(@Param("camaraId") Long camaraId);

    @Modifying
    @Query("UPDATE Sensor s SET s.estado = io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor.DESHABILITADO, s.estadoPrevio = s.estado " +
            "WHERE s.camara.sucursal.id = :sucursalId AND s.estado <> io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor.DESHABILITADO")
    int bulkDeshabilitarPorSucursal(@Param("sucursalId") Long sucursalId);

    @Modifying
    @Query("UPDATE Sensor s SET s.estado = io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor.DESHABILITADO, s.estadoPrevio = s.estado " +
            "WHERE s.camara.sucursal.empresa.id = :empresaId AND s.estado <> io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor.DESHABILITADO")
    int bulkDeshabilitarPorEmpresa(@Param("empresaId") Long empresaId);

    @Modifying
    @Query("UPDATE Sensor s SET s.estado = s.estadoPrevio, s.estadoPrevio = NULL " +
            "WHERE s.camara.id = :camaraId AND s.estado = io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor.DESHABILITADO AND s.estadoPrevio IS NOT NULL")
    int bulkActivarPorCamara(@Param("camaraId") Long camaraId);

    @Modifying
    @Query("UPDATE Sensor s SET s.estado = s.estadoPrevio, s.estadoPrevio = NULL " +
            "WHERE s.camara.sucursal.id = :sucursalId AND s.estado = io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor.DESHABILITADO AND s.estadoPrevio IS NOT NULL")
    int bulkActivarPorSucursal(@Param("sucursalId") Long sucursalId);

    @Modifying
    @Query("UPDATE Sensor s SET s.estado = s.estadoPrevio, s.estadoPrevio = NULL " +
            "WHERE s.camara.sucursal.empresa.id = :empresaId AND s.estado = io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor.DESHABILITADO AND s.estadoPrevio IS NOT NULL")
    int bulkActivarPorEmpresa(@Param("empresaId") Long empresaId);
}