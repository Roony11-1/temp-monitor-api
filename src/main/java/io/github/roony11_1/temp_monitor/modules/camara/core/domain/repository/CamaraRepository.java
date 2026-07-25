package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CamaraRepository extends JpaRepository<Camara, Long>, JpaSpecificationExecutor<Camara> 
{
    List<Camara> findBySucursalId(Long sucursalId);

    Page<Camara> findBySucursalId(Long sucursalId, Pageable pageable);

    long countByActivo(boolean activo);
}
