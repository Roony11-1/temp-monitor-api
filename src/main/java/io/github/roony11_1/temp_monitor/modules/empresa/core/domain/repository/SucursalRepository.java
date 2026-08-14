package io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
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

public interface SucursalRepository extends JpaRepository<Sucursal, Long>, JpaSpecificationExecutor<Sucursal> 
{
    @Override
    @EntityGraph(attributePaths = "empresa")
    Page<Sucursal> findAll(Specification<Sucursal> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "empresa")
    List<Sucursal> findAll(Specification<Sucursal> spec);

    boolean existsByNombre(String nombre);

    @Modifying
    @Query("UPDATE Sucursal s SET s.deletedAt = :deletedAt WHERE s.empresa.id = :empresaId")
    int bulkActualizarDeletedAtPorEmpresa(@Param("empresaId") Long empresaId, @Param("deletedAt") Instant deletedAt);

    @Modifying
    @Query("UPDATE Sucursal s SET s.activo = :activo WHERE s.empresa.id = :empresaId")
    int bulkActualizarActivoPorEmpresa(@Param("empresaId") Long empresaId, @Param("activo") boolean activo);
}
