package io.github.roony11_1.temp_monitor.kernel.security.scope.strategy;

import io.github.roony11_1.specification.core.FilterCondition;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

/**
 * Estrategia OCP para resolver ámbito de acceso.
 * Cada implementación encapsula la lógica de filtrado y autorización para un tipo de usuario.
 * Agregar un nuevo tipo de ámbito (ej. MultiSucursal, Auditor) solo requiere una nueva implementación,
 * sin modificar {@code CurrentUserScope}.
 */
public interface ScopeStrategy {

    Optional<FilterCondition> scopeCondition(String empresaPath, String sucursalPath);

    Optional<FilterCondition> empresaOnlyCondition(String empresaPath);

    <T> Specification<T> scopeSpec(String empresaPath, String sucursalPath);

    <T> Specification<T> empresaOnlySpec(String empresaPath);

    boolean canAccess(Long sucursalId, Long empresaId);
}
