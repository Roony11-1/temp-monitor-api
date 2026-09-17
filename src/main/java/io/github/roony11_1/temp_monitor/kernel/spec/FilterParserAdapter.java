package io.github.roony11_1.temp_monitor.kernel.spec;

import io.github.roony11_1.specification.core.FilterConditions;
import io.github.roony11_1.specification.queryparams.QueryParamsFilterParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * Adaptador OCP para parsing de query params a {@link FilterConditions}.
 *
 * <p>Encapsula la separación introducida en roony-specification-spring 1.1.0:
 * <ul>
 *   <li>ANTES: {@code FilterSpecificationBuilder.withAliases(...).withConditions(Map)}</li>
 *   <li>AHORA: {@code QueryParamsFilterParser.parse(Map, aliases)} -> {@code FilterConditions} -> {@code FilterSpecificationBuilder}</li>
 * </ul>
 * Centraliza aliases y el ignore automático de {@code page/size/sort}.
 *
 * <p>Abierto a extensión: nuevo alias o entidad = agregar entrada en
 * {@code app.filter-aliases.*} o registrar un nuevo método con aliases distintos,
 * sin modificar Services existentes. Cerrado a modificación.
 */
@Component
@RequiredArgsConstructor
public class FilterParserAdapter {

    private final FilterAliasRegistry aliasRegistry;

    /**
     * Parsea filtros crudos usando los aliases registrados para la entidad.
     * Si no hay aliases registrados, delega sin aliases.
     * @param raw filtros crudos {@code Map<String,String>} del controller
     * @param entityKey clave de entidad (ej. "sucursal", "camara", "usuario", "sensor")
     */
    public FilterConditions parse(Map<String, String> raw, String entityKey) {
        if (raw == null || raw.isEmpty()) {
            return QueryParamsFilterParser.parse(Collections.emptyMap());
        }
        Map<String, String> aliases = aliasRegistry.aliasesFor(entityKey);
        if (aliases == null || aliases.isEmpty()) {
            return QueryParamsFilterParser.parse(raw);
        }
        return QueryParamsFilterParser.parse(raw, aliases);
    }

    /**
     * Parsea sin aliases (para entidades que no exponen alias).
     */
    public FilterConditions parse(Map<String, String> raw) {
        if (raw == null || raw.isEmpty()) {
            return QueryParamsFilterParser.parse(Collections.emptyMap());
        }
        return QueryParamsFilterParser.parse(raw);
    }

    /**
     * Parsea con aliases explícitos (uso puntual, no requiere registro global).
     */
    public FilterConditions parse(Map<String, String> raw, Map<String, String> aliases) {
        if (raw == null || raw.isEmpty()) {
            return QueryParamsFilterParser.parse(Collections.emptyMap());
        }
        if (aliases == null || aliases.isEmpty()) {
            return QueryParamsFilterParser.parse(raw);
        }
        return QueryParamsFilterParser.parse(raw, aliases);
    }
}
