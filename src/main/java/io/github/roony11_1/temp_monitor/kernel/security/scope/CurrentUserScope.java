package io.github.roony11_1.temp_monitor.kernel.security.scope;

import io.github.roony11_1.temp_monitor.kernel.security.exception.AccesoDenegadoException;
import io.github.roony11_1.temp_monitor.kernel.security.exception.NoAutenticadoException;
import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.specification.core.FilterCondition;
import io.github.roony11_1.specification.core.FilterOperator;
import io.github.roony11_1.specification.spring.FilterSpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Resuelve el ámbito de acceso del usuario autenticado.
 *
 * Política:
 * - SUPER_ADMIN -> acceso total (sin filtro).
 * - Usuario con sucursal (ADMIN_SUCURSAL, TECNICO, USUARIO) -> solo su sucursal.
 * - Usuario con solo empresa (ADMIN_EMPRESA) -> solo su empresa.
 * - Sin empresa ni sucursal -> denegado.
 */
@Component
public class CurrentUserScope 
{
    public TokenUser currentUser()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof TokenUser user))
        {
            throw new NoAutenticadoException("Usuario no autenticado");
        }
        return user;
    }

    public boolean isSuperAdmin()
    {
        return currentUser().roles().contains(Rol.SUPER_ADMIN);
    }

    /**
     * Condición de ámbito para entidades anidadas jerárquicamente
     * (p.ej. Camara, Sucursal, Usuario) donde el súper ve todo y el resto ve
     * por sucursal si la tiene, si no por empresa.
     */
    public Optional<FilterCondition> scopeCondition(String empresaPath, String sucursalPath)
    {
        if (isSuperAdmin())
        {
            return Optional.empty();
        }
        TokenUser user = currentUser();
        if (user.sucursalId() != null)
        {
            return Optional.of(new FilterCondition(sucursalPath, FilterOperator.EQ, user.sucursalId()));
        }
        if (user.empresaId() != null)
        {
            return Optional.of(new FilterCondition(empresaPath, FilterOperator.EQ, user.empresaId()));
        }
        throw new AccesoDenegadoException("El usuario no tiene un ámbito de acceso asignado");
    }

    /**
     * Condición de ámbito para el catálogo de Empresas: ambos roles acotados
     * ven únicamente su propia empresa.
     */
    public Optional<FilterCondition> scopeEmpresaOnly(String empresaPath)
    {
        if (isSuperAdmin())
        {
            return Optional.empty();
        }
        TokenUser user = currentUser();
        if (user.empresaId() != null)
        {
            return Optional.of(new FilterCondition(empresaPath, FilterOperator.EQ, user.empresaId()));
        }
        throw new AccesoDenegadoException("El usuario no tiene un ámbito de acceso asignado");
    }

    /**
     * Specification de ámbito lista para combinarse con la del usuario.
     * Devuelve una conjunción (sin filtro) para SUPER_ADMIN.
     * Para el resto de roles, además del ámbito, excluye los registros
     * eliminados lógicamente (deletedAt IS NULL), de modo que solo el
     * SUPER_ADMIN ve el estado "eliminado" en el mismo listado.
     */
    public <T> Specification<T> scopeSpec(String empresaPath, String sucursalPath)
    {
        if (isSuperAdmin())
        {
            return (root, query, cb) -> cb.conjunction();
        }
        Optional<FilterCondition> condition = scopeCondition(empresaPath, sucursalPath);
        FilterSpecificationBuilder<T> builder = new FilterSpecificationBuilder<T>()
                .withCondition(new FilterCondition("deletedAt", FilterOperator.IS_NULL, null));
        condition.ifPresent(builder::withCondition);
        return builder.build();
    }

    /**
     * Specification de ámbito para el catálogo de Empresas.
     * Devuelve una conjunción para SUPER_ADMIN; para el resto excluye
     * los registros eliminados lógicamente.
     */
    public <T> Specification<T> scopeEmpresaOnlySpec(String empresaPath)
    {
        if (isSuperAdmin())
        {
            return (root, query, cb) -> cb.conjunction();
        }
        Optional<FilterCondition> condition = scopeEmpresaOnly(empresaPath);
        FilterSpecificationBuilder<T> builder = new FilterSpecificationBuilder<T>()
                .withCondition(new FilterCondition("deletedAt", FilterOperator.IS_NULL, null));
        condition.ifPresent(builder::withCondition);
        return builder.build();
    }

    /**
     * Comprueba acceso puntual sobre una entidad ya cargada.
     * Los sensores sin cámara (sucursalId/empresaId null) solo los ve SUPER_ADMIN.
     */
    public boolean canAccess(Long sucursalId, Long empresaId)
    {
        if (isSuperAdmin())
        {
            return true;
        }
        TokenUser user = currentUser();
        if (user.sucursalId() != null)
        {
            return sucursalId != null && user.sucursalId().equals(sucursalId);
        }
        if (user.empresaId() != null)
        {
            return empresaId != null && user.empresaId().equals(empresaId);
        }
        return false;
    }

    public void assertAccess(Long sucursalId, Long empresaId)
    {
        if (!canAccess(sucursalId, empresaId))
        {
            throw new AccesoDenegadoException("No tiene acceso al recurso solicitado");
        }
    }
}