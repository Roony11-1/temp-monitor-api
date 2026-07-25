package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CamaraRepository extends JpaRepository<Camara, Long> 
{
    List<Camara> findBySucursalId(Long sucursalId);

    Page<Camara> findBySucursalId(Long sucursalId, Pageable pageable);

    long countByActivo(boolean activo);
}
