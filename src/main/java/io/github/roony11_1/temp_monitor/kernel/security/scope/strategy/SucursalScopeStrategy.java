package io.github.roony11_1.temp_monitor.kernel.security.scope.strategy;

import io.github.roony11_1.specification.core.FilterCondition;
import io.github.roony11_1.specification.core.FilterOperator;
import io.github.roony11_1.specification.spring.FilterSpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public class SucursalScopeStrategy implements ScopeStrategy {

    private final Long sucursalId;
    private final Long empresaId;

    public SucursalScopeStrategy(Long sucursalId, Long empresaId) {
        this.sucursalId = sucursalId;
        this.empresaId = empresaId;
    }

    @Override
    public Optional<FilterCondition> scopeCondition(String empresaPath, String sucursalPath) {
        return Optional.of(new FilterCondition(sucursalPath, FilterOperator.EQ, sucursalId));
    }

    @Override
    public Optional<FilterCondition> empresaOnlyCondition(String empresaPath) {
        // Para listado de empresas, un usuario de sucursal ve solo su empresa
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
        return sucursalId != null && this.sucursalId.equals(sucursalId);
    }
}
