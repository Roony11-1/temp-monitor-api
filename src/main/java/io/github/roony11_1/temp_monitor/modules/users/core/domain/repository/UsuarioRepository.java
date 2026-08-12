package io.github.roony11_1.temp_monitor.modules.users.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> 
{
    boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.empresa LEFT JOIN FETCH u.sucursal WHERE u.empresa.id = :empresaId")
    List<Usuario> findByEmpresa_Id(@Param("empresaId") Long empresaId);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa JOIN FETCH u.sucursal WHERE u.sucursal.id = :sucursalId")
    List<Usuario> findBySucursal_Id(@Param("sucursalId") Long sucursalId);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa LEFT JOIN FETCH u.sucursal")
    @Override
    List<Usuario> findAll();

    @Query(value = "SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa LEFT JOIN FETCH u.sucursal",
           countQuery = "SELECT COUNT(u) FROM Usuario u")
    @Override
    Page<Usuario> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"empresa", "sucursal"})
    Page<Usuario> findAll(Specification<Usuario> spec, Pageable pageable);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa LEFT JOIN FETCH u.sucursal WHERE u.id = :id")
    @Override
    Optional<Usuario> findById(@Param("id") Long id);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa LEFT JOIN FETCH u.sucursal WHERE u.email = :email")
    Optional<Usuario> findByEmail(@Param("email") String email);
}
