package io.github.roony11_1.temp_monitor.kernel.specification;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FilterSpecification {

    private static final Set<String> EXCLUDED_PARAMS = Set.of("page", "size", "sort");

    public static <T> Specification<T> from(Map<String, String> filters) {
        Map<String, String> entityFilters = new HashMap<>();
        if (filters != null) {
            for (var entry : filters.entrySet()) {
                if (!EXCLUDED_PARAMS.contains(entry.getKey())) {
                    entityFilters.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return (root, query, cb) -> {
            if (entityFilters.isEmpty()) return cb.conjunction();

            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String> entry : entityFilters.entrySet()) {
                String field = entry.getKey();
                String value = entry.getValue();
                if (value == null || value.isEmpty()) continue;

                Path<?> path = resolvePath(root, field);

                if (path.getJavaType() != null && path.getJavaType().isEnum()) {
                    @SuppressWarnings("unchecked")
                    Object enumValue = Enum.valueOf((Class<Enum>) path.getJavaType(), value);
                    predicates.add(cb.equal(path, enumValue));
                } else {
                    predicates.add(cb.equal(path, value));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> Path<Object> resolvePath(Path<T> root, String field) {
        String[] parts = field.split("\\.");
        Path<?> path = root;
        for (String part : parts) {
            path = path.get(part);
        }
        return (Path<Object>) path;
    }
}
