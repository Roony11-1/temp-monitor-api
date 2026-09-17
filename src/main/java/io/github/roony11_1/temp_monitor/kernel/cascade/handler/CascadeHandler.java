package io.github.roony11_1.temp_monitor.kernel.cascade.handler;

import java.time.Instant;

/**
 * OCP: cada jerarquía (Empresa, Sucursal, Cámara, futuro Zona/Dispositivo) tiene su handler.
 * Agregar un nuevo nivel solo requiere un nuevo bean que implemente esta interfaz,
 * sin modificar {@code CascadeStateService}.
 */
public interface CascadeHandler<T> {

    Class<T> supportedType();

    void softDelete(T root, Instant now);

    void restore(T root);

    void deactivate(T root);

    void activate(T root);
}
