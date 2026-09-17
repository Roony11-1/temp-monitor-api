package io.github.roony11_1.temp_monitor.kernel.security.scope.strategy;

import io.github.roony11_1.specification.core.FilterCondition;
import io.github.roony11_1.specification.core.FilterOperator;
import io.github.roony11_1.specification.spring.FilterSpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public class EmpresaScopeStrategy implements ScopeStrategy {

    private final Long empresaId;

    public EmpresaScopeStrategy(Long empresaId) {
        this.empresaId = empresaId;
    }

    @Override
    public Optional<FilterCondition> scopeCondition(String empresaPath, String sucursalPath) {
        return Optional.of(new FilterCondition(empresaPath, FilterOperator.EQ, empresaId));
    }

    @Override
    public Optional<FilterCondition> empresaOnlyCondition(String empresaPath) {
        return Optional.of(new FilterCondition(empresaPath, FilterOperator.EQ, empresaId));
    }

    @Override
    public <T> Specification<T> scopeSpec(String empresaPath, String sucursalPath) {
        FilterSpecificationBuilder<T> builder = new FilterSpecificationBuilder<T>()
                .withCondition(new FilterCondition("deletedAt", FilterOperator.IS_NULL, null));
        scopeCondition(empresaPath, sucursalPath).ifPresent(builder::withCondition);
        return builder.build();
    }

    @Override
    public <T> Specification<T> empresaOnlySpec(String empresaPath) {
        FilterSpecificationBuilder<T> builder = new FilterSpecificationBuilder<T>()
                .withCondition(new FilterCondition("deletedAt", FilterOperator.IS_NULL, null));
        empresaOnlyCondition(empresaPath).ifPresent(builder::withCondition);
        return builder.build();
    }

    @Override
    public boolean canAccess(Long sucursalId, Long empresaId) {
        return empresaId != null && this.empresaId.equals(empresaId);
    }
}
