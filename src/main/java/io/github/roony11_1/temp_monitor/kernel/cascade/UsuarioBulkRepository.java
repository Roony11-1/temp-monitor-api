package io.github.roony11_1.temp_monitor.kernel.cascade;

import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

/**
 * Bulk de estado para {@link Usuario} usado por {@link CascadeStateService}.
 * Los repositorios de usuario deben extenderla para heredar estas operaciones.
 */
@NoRepositoryBean
public interface UsuarioBulkRepository extends JpaRepository<Usuario, Long>
{
    @Modifying
    @Query("UPDATE Usuario u SET u.deletedAt = :deletedAt WHERE u.empresa.id = :empresaId")
    int bulkActualizarDeletedAtPorEmpresa(@Param("empresaId") Long empresaId, @Param("deletedAt") Instant deletedAt);

    @Modifying
    @Query("UPDATE Usuario u SET u.deletedAt = :deletedAt WHERE u.sucursal.id = :sucursalId")
    int bulkActualizarDeletedAtPorSucursal(@Param("sucursalId") Long sucursalId, @Param("deletedAt") Instant deletedAt);

    @Modifying
    @Query("UPDATE Usuario u SET u.activo = :activo WHERE u.empresa.id = :empresaId")
    int bulkActualizarActivoPorEmpresa(@Param("empresaId") Long empresaId, @Param("activo") boolean activo);

    @Modifying
    @Query("UPDATE Usuario u SET u.activo = :activo WHERE u.sucursal.id = :sucursalId")
    int bulkActualizarActivoPorSucursal(@Param("sucursalId") Long sucursalId, @Param("activo") boolean activo);
}