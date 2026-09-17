package io.github.roony11_1.temp_monitor.kernel.security.scope;

import io.github.roony11_1.temp_monitor.kernel.security.exception.AccesoDenegadoException;
import io.github.roony11_1.temp_monitor.kernel.security.exception.NoAutenticadoException;
import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.specification.core.FilterCondition;
import io.github.roony11_1.temp_monitor.kernel.security.scope.strategy.EmpresaScopeResolver;
import io.github.roony11_1.temp_monitor.kernel.security.scope.strategy.ScopeStrategy;
import io.github.roony11_1.temp_monitor.kernel.security.scope.strategy.ScopeStrategyFactory;
import io.github.roony11_1.temp_monitor.kernel.security.scope.strategy.SucursalScopeResolver;
import io.github.roony11_1.temp_monitor.kernel.security.scope.strategy.SuperAdminScopeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Resuelve el ámbito de acceso del usuario autenticado.
 *
 * <p>OCP: delega a {@link ScopeStrategy} resuelta por {@link ScopeStrategyFactory}
 * vía cadena de {@code ScopeStrategyResolver}s. Agregar nuevo tipo de ámbito
 * (ej. multi-sucursal, auditor) solo requiere un nuevo resolver bean, sin modificar
 * esta clase ni duplicar if-else en 4 métodos.
 *
 * <p>Política original preservada:
 * - SUPER_ADMIN -> acceso total (sin filtro).
 * - Usuario con sucursal -> solo su sucursal.
 * - Usuario con solo empresa -> solo su empresa.
 * - Sin empresa ni sucursal -> denegado.
 */
@Component
public class CurrentUserScope 
{
    private final ScopeStrategyFactory factory;

    @Autowired
    public CurrentUserScope(ScopeStrategyFactory factory) {
        this.factory = factory;
    }

    /**
     * Constructor para uso en tests sin Spring (mantiene compatibilidad con {@code new CurrentUserScope()}).
     * Construye un factory con los resolvers por defecto ordenados.
     */
    public CurrentUserScope() {
        this(new ScopeStrategyFactory(List.of(
                new SuperAdminScopeResolver(),
                new SucursalScopeResolver(),
                new EmpresaScopeResolver()
        )));
    }

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

    private ScopeStrategy strategy() {
        return factory.resolve(currentUser());
    }

    /**
     * Condición de ámbito para entidades anidadas jerárquicamente
     * (p.ej. Camara, Sucursal, Usuario) donde el súper ve todo y el resto ve
     * por sucursal si la tiene, si no por empresa.
     */
    public Optional<FilterCondition> scopeCondition(String empresaPath, String sucursalPath)
    {
        return strategy().scopeCondition(empresaPath, sucursalPath);
    }

    /**
     * Condición de ámbito para el catálogo de Empresas: ambos roles acotados
     * ven únicamente su propia empresa.
     */
    public Optional<FilterCondition> scopeEmpresaOnly(String empresaPath)
    {
        return strategy().empresaOnlyCondition(empresaPath);
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
        return strategy().scopeSpec(empresaPath, sucursalPath);
    }

    /**
     * Specification de ámbito para el catálogo de Empresas.
     * Devuelve una conjunción para SUPER_ADMIN; para el resto excluye
     * los registros eliminados lógicamente.
     */
    public <T> Specification<T> scopeEmpresaOnlySpec(String empresaPath)
    {
        return strategy().empresaOnlySpec(empresaPath);
    }

    /**
     * Comprueba acceso puntual sobre una entidad ya cargada.
     * Los sensores sin cámara (sucursalId/empresaId null) solo los ve SUPER_ADMIN.
     */
    public boolean canAccess(Long sucursalId, Long empresaId)
    {
        try {
            return strategy().canAccess(sucursalId, empresaId);
        } catch (AccesoDenegadoException e) {
            return false;
        }
    }

    public void assertAccess(Long sucursalId, Long empresaId)
    {
        if (!canAccess(sucursalId, empresaId))
        {
            throw new AccesoDenegadoException("No tiene acceso al recurso solicitado");
        }
    }
}
