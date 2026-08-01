package io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SucursalRepository extends JpaRepository<Sucursal, Long>, JpaSpecificationExecutor<Sucursal> 
{
    List<Sucursal> findByEmpresaId(Long empresaId);

    Page<Sucursal> findByEmpresaId(Long empresaId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "empresa")
    Page<Sucursal> findAll(Specification<Sucursal> spec, Pageable pageable);

    boolean existsByNombre(String nombre);
}
