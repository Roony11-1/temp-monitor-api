package io.github.roony11_1.temp_monitor.modules.users.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
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
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> 
{
    boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa LEFT JOIN FETCH u.sucursal WHERE u.deletedAt IS NULL")
    @Override
    List<Usuario> findAll();

    @Query(value = "SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa LEFT JOIN FETCH u.sucursal WHERE u.deletedAt IS NULL",
           countQuery = "SELECT COUNT(u) FROM Usuario u WHERE u.deletedAt IS NULL")
    @Override
    Page<Usuario> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"empresa", "sucursal"})
    Page<Usuario> findAll(Specification<Usuario> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"empresa", "sucursal"})
    List<Usuario> findAll(Specification<Usuario> spec);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa LEFT JOIN FETCH u.sucursal WHERE u.id = :id AND u.deletedAt IS NULL")
    @Override
    Optional<Usuario> findById(@Param("id") Long id);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa LEFT JOIN FETCH u.sucursal WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<Usuario> findByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE Usuario u SET u.deletedAt = :deletedAt WHERE u.empresa.id = :empresaId")
    int bulkActualizarDeletedAtPorEmpresa(@Param("empresaId") Long empresaId, @Param("deletedAt") Instant deletedAt);

    @Modifying
    @Query("UPDATE Usuario u SET u.deletedAt = :deletedAt WHERE u.sucursal.id = :sucursalId")
    int bulkActualizarDeletedAtPorSucursal(@Param("sucursalId") Long sucursalId, @Param("deletedAt") Instant deletedAt);

    @Modifying
    @Query("UPDATE Usuario u SET u.activo = :activo WHERE u.empresa.id = :empresaId")
    int bulkActualizarActivoPorEmpresa(@Param("empresaId") Long empresaId, @Param("activo") boolean activo);

    @Modifying
    @Query("UPDATE Usuario u SET u.activo = :activo WHERE u.sucursal.id = :sucursalId")
    int bulkActualizarActivoPorSucursal(@Param("sucursalId") Long sucursalId, @Param("activo") boolean activo);
}
