package io.github.roony11_1.temp_monitor.kernel.cascade;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

/**
 * Bulk de estado para {@link Camara} usado por {@link CascadeStateService}.
 * Los repositorios de cámara deben extenderla para heredar estas operaciones.
 */
@NoRepositoryBean
public interface CamaraBulkRepository extends JpaRepository<Camara, Long>
{
    @Modifying
    @Query("UPDATE Camara c SET c.deletedAt = :deletedAt WHERE c.sucursal.id = :sucursalId")
    int bulkActualizarDeletedAtPorSucursal(@Param("sucursalId") Long sucursalId, @Param("deletedAt") Instant deletedAt);

    @Modifying
    @Query("UPDATE Camara c SET c.deletedAt = :deletedAt WHERE c.sucursal.empresa.id = :empresaId")
    int bulkActualizarDeletedAtPorEmpresa(@Param("empresaId") Long empresaId, @Param("deletedAt") Instant deletedAt);

    @Modifying
    @Query("UPDATE Camara c SET c.activo = :activo WHERE c.sucursal.id = :sucursalId")
    int bulkActualizarActivoPorSucursal(@Param("sucursalId") Long sucursalId, @Param("activo") boolean activo);

    @Modifying
    @Query("UPDATE Camara c SET c.activo = :activo WHERE c.sucursal.empresa.id = :empresaId")
    int bulkActualizarActivoPorEmpresa(@Param("empresaId") Long empresaId, @Param("activo") boolean activo);
}