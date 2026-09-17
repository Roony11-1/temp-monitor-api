package io.github.roony11_1.temp_monitor.kernel.cascade;

import io.github.roony11_1.temp_monitor.kernel.cascade.handler.CascadeHandler;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Propaga cambios de estado en cascada por la jerarquía de negocio:
 * Empresa -> Sucursal -> Camara -> Sensor, más Usuarios ligados a empresa/sucursal.
 *
 * <p>OCP: ahora delega a {@link CascadeHandler}s. Agregar un nuevo nivel (ej. Zona, Dispositivo)
 * solo requiere un nuevo bean {@code CascadeHandler}, sin modificar esta clase.
 * Cada handler encapsula los bulk updates de su jerarquía.
 *
 * <p>Cada método es {@code @Transactional} (REQUIRED: si ya vino de un service transaccional, se une a esa transacción).
 */
@Component
@RequiredArgsConstructor
@Transactional
public class CascadeStateService 
{
    private final List<CascadeHandler<?>> handlers;

    private Map<Class<?>, CascadeHandler<?>> handlerMap;

    @PostConstruct
    void init() {
        handlerMap = handlers.stream()
                .collect(Collectors.toMap(CascadeHandler::supportedType, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    private <T> CascadeHandler<T> handlerFor(Class<T> type) {
        CascadeHandler<?> handler = handlerMap.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("No hay CascadeHandler para " + type.getSimpleName());
        }
        return (CascadeHandler<T>) handler;
    }

    // ===================== Empresa =====================

    public void eliminarEmpresa(Empresa empresa) 
    {
        handlerFor(Empresa.class).softDelete(empresa, Instant.now());
    }

    public void restaurarEmpresa(Empresa empresa) 
    {
        handlerFor(Empresa.class).restore(empresa);
    }

    public void desactivarEmpresa(Empresa empresa) 
    {
        handlerFor(Empresa.class).deactivate(empresa);
    }

    public void activarEmpresa(Empresa empresa) 
    {
        handlerFor(Empresa.class).activate(empresa);
    }

    // ===================== Sucursal =====================

    public void eliminarSucursal(Sucursal sucursal) 
    {
        handlerFor(Sucursal.class).softDelete(sucursal, Instant.now());
    }

    public void restaurarSucursal(Sucursal sucursal) 
    {
        handlerFor(Sucursal.class).restore(sucursal);
    }

    public void desactivarSucursal(Sucursal sucursal) 
    {
        handlerFor(Sucursal.class).deactivate(sucursal);
    }

    public void activarSucursal(Sucursal sucursal) 
    {
        handlerFor(Sucursal.class).activate(sucursal);
    }

    // ===================== Camara =====================

    public void eliminarCamara(Camara camara) 
    {
        handlerFor(Camara.class).softDelete(camara, Instant.now());
    }

    public void restaurarCamara(Camara camara) 
    {
        handlerFor(Camara.class).restore(camara);
    }

    public void desactivarCamara(Camara camara) 
    {
        handlerFor(Camara.class).deactivate(camara);
    }

    public void activarCamara(Camara camara) 
    {
        handlerFor(Camara.class).activate(camara);
    }

    // ===================== Genérico OCP =====================

    /**
     * Método genérico OCP para futuras entidades. No requiere nuevo método por tipo.
     * Ej.: {@code cascadeStateService.softDelete(zona)} si existe un handler para Zona.
     */
    public <T> void softDelete(T root, Instant now) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) root.getClass();
        // Soporta proxies de Hibernate: busca por superclase si no encuentra exacto
        CascadeHandler<T> handler = findHandlerForInstance(root);
        handler.softDelete(root, now);
    }

    @SuppressWarnings("unchecked")
    private <T> CascadeHandler<T> findHandlerForInstance(T instance) {
        Class<?> clazz = instance.getClass();
        // Intenta exacto, luego superclase (para proxies)
        CascadeHandler<?> handler = handlerMap.get(clazz);
        if (handler == null) {
            for (Map.Entry<Class<?>, CascadeHandler<?>> e : handlerMap.entrySet()) {
                if (e.getKey().isAssignableFrom(clazz) || clazz.getName().contains(e.getKey().getSimpleName())) {
                    handler = e.getValue();
                    break;
                }
            }
        }
        if (handler == null) {
            throw new IllegalArgumentException("No hay CascadeHandler para " + clazz.getSimpleName());
        }
        return (CascadeHandler<T>) handler;
    }
}
