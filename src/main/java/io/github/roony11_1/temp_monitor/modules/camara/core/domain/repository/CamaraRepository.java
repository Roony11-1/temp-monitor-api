package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface CamaraRepository extends JpaRepository<Camara, Long>, JpaSpecificationExecutor<Camara> 
{
    @Override
    @EntityGraph(attributePaths = "sucursal")
    Page<Camara> findAll(Specification<Camara> spec, Pageable pageable);

    long countByActivo(boolean activo);

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
