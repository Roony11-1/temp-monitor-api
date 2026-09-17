package io.github.roony11_1.temp_monitor.kernel.spec;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Registro centralizado de aliases para filtros.
 *
 * <p>Principio Abierto/Cerrado: agregar una nueva entidad o alias no requiere
 * modificar los Services que ya usan {@link FilterParserAdapter}; basta con
 * registrar aquí o vía {@code app.filter-aliases} en el futuro.
 *
 * <p>Aliases actuales replican exactamente los que estaban duplicados en cada Service:
 * <ul>
 *   <li>usuario: empresa -> empresa.nombre, sucursal -> sucursal.nombre</li>
 *   <li>sucursal: empresa -> empresa.nombre</li>
 *   <li>camara: sucursal -> sucursal.nombre, estado -> activo</li>
 *   <li>sensor: empresaNombre -> camara.sucursal.empresa.nombre, sucursalNombre -> camara.sucursal.nombre</li>
 *   <li>empresa: sin alias</li>
 * </ul>
 */
@Component
public class FilterAliasRegistry {

    private final Map<String, Map<String, String>> registry;

    public FilterAliasRegistry() {
        Map<String, Map<String, String>> map = new HashMap<>();
        map.put("usuario", Map.of(
                "empresa", "empresa.nombre",
                "sucursal", "sucursal.nombre"));
        map.put("sucursal", Map.of(
                "empresa", "empresa.nombre"));
        map.put("camara", Map.of(
                "sucursal", "sucursal.nombre",
                "estado", "activo"));
        map.put("sensor", Map.of(
                "empresaNombre", "camara.sucursal.empresa.nombre",
                "sucursalNombre", "camara.sucursal.nombre"));
        map.put("empresa", Collections.emptyMap());
        this.registry = Collections.unmodifiableMap(map);
    }

    public Map<String, String> aliasesFor(String entityKey) {
        return registry.getOrDefault(entityKey, Collections.emptyMap());
    }

    /**
     * Permite registrar dinámicamente aliases para nuevas entidades sin modificar esta clase
     * (extensión programática). Para tests o plugins.
     */
    public Map<String, Map<String, String>> all() {
        return registry;
    }
}
