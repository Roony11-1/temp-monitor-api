package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CamaraRepository extends JpaRepository<Camara, Long>, JpaSpecificationExecutor<Camara> 
{
    @Override
    @EntityGraph(attributePaths = "sucursal")
    Page<Camara> findAll(Specification<Camara> spec, Pageable pageable);

    long countByActivo(boolean activo);
}
