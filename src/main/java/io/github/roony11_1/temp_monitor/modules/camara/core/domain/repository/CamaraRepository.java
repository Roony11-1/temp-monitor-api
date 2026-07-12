package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CamaraRepository extends JpaRepository<Camara, Long> 
{
    List<Camara> findBySucursalId(Long sucursalId);

    long countByActivo(boolean activo);
}
