package io.github.roony11_1.temp_monitor.kernel.cascade;

import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

/**
 * Bulk de estado para {@link Sucursal} usado por {@link CascadeStateService}.
 * Los repositorios de sucursal deben extenderla para heredar estas operaciones.
 */
@NoRepositoryBean
public interface SucursalBulkRepository extends JpaRepository<Sucursal, Long>
{
    @Modifying
    @Query("UPDATE Sucursal s SET s.deletedAt = :deletedAt WHERE s.empresa.id = :empresaId")
    int bulkActualizarDeletedAtPorEmpresa(@Param("empresaId") Long empresaId, @Param("deletedAt") Instant deletedAt);

    @Modifying
    @Query("UPDATE Sucursal s SET s.activo = :activo WHERE s.empresa.id = :empresaId")
    int bulkActualizarActivoPorEmpresa(@Param("empresaId") Long empresaId, @Param("activo") boolean activo);
}