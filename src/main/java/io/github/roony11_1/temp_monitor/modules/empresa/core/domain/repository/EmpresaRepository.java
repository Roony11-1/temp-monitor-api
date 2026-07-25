package io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long>, JpaSpecificationExecutor<Empresa> 
{
    Optional<Empresa> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
