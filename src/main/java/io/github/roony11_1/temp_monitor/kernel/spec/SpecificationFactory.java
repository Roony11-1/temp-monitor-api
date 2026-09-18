package io.github.roony11_1.temp_monitor.kernel.spec;

import io.github.roony11_1.specification.core.FilterCondition;
import io.github.roony11_1.specification.core.FilterOperator;
import io.github.roony11_1.specification.spring.FilterSpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;

/**
 * Factory DRY para Specifications repetidas en Services.
 * Centraliza {@code byId}, {@code byEmpresa}, {@code bySucursal} y
 * evita que cada Service reimplemente los mismos 3 helpers.
 * OCP: agregar un nuevo filtro por entidad solo requiere un nuevo método aquí.
 */
public final class SpecificationFactory {

    private SpecificationFactory() {}

    public static <T> Specification<T> byId(Long id) {
        return new FilterSpecificationBuilder<T>()
                .withCondition(new FilterCondition("id", FilterOperator.EQ, id))
                .build();
    }

    public static <T> Specification<T> byField(String field, Object value) {
        return new FilterSpecificationBuilder<T>()
                .withCondition(new FilterCondition(field, FilterOperator.EQ, value))
                .build();
    }

    public static <T> Specification<T> byEmpresaId(Long empresaId) {
        return byField("empresa.id", empresaId);
    }

    public static <T> Specification<T> byEmpresaId(String path, Long empresaId) {
        return byField(path, empresaId);
    }

    public static <T> Specification<T> bySucursalId(Long sucursalId) {
        return byField("sucursal.id", sucursalId);
    }

    public static <T> Specification<T> bySucursalId(String path, Long sucursalId) {
        return byField(path, sucursalId);
    }

    public static <T> Specification<T> notDeleted() {
        return new FilterSpecificationBuilder<T>()
                .withCondition(new FilterCondition("deletedAt", FilterOperator.IS_NULL, null))
                .build();
    }
}
