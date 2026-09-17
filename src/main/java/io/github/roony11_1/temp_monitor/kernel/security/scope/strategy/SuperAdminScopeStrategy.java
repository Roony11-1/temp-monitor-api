package io.github.roony11_1.temp_monitor.kernel.security.scope.strategy;

import io.github.roony11_1.specification.core.FilterCondition;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public class SuperAdminScopeStrategy implements ScopeStrategy {

    @Override
    public Optional<FilterCondition> scopeCondition(String empresaPath, String sucursalPath) {
        return Optional.empty();
    }

    @Override
    public Optional<FilterCondition> empresaOnlyCondition(String empresaPath) {
        return Optional.empty();
    }

    @Override
    public <T> Specification<T> scopeSpec(String empresaPath, String sucursalPath) {
        return (root, query, cb) -> cb.conjunction();
    }

    @Override
    public <T> Specification<T> empresaOnlySpec(String empresaPath) {
        return (root, query, cb) -> cb.conjunction();
    }

    @Override
    public boolean canAccess(Long sucursalId, Long empresaId) {
        return true;
    }
}
