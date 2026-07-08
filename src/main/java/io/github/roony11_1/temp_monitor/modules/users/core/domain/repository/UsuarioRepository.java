package io.github.roony11_1.temp_monitor.modules.users.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> 
{
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByEmpresaId(Long empresaId);

    List<Usuario> findBySucursalId(Long sucursalId);
}
