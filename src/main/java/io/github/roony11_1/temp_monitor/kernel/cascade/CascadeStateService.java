package io.github.roony11_1.temp_monitor.kernel.cascade;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Propaga cambios de estado en cascada por la jerarquía de negocio:
 * Empresa -> Sucursal -> Camara -> Sensor, más Usuarios ligados a empresa/sucursal.
 *
 * <p>Acciones soportadas por nivel:
 * <ul>
 *   <li>{@code eliminar*}: marca {@code deletedAt} en el nodo y todo su árbol.</li>
 *   <li>{@code restaurar*}: limpia {@code deletedAt} en el nodo y todo su árbol.</li>
 *   <li>{@code desactivar*}: {@code activo=false} en el nodo y sus hijos; para sensores,
 *       {@code estado=DESHABILITADO} guardando su estado anterior en {@code estadoPrevio}
 *       (solo si aún no estaban deshabilitados).</li>
 *   <li>{@code activar*}: {@code activo=true} en el nodo y sus hijos; para sensores
 *       deshabilitados por la cascada ({@code estadoPrevio != null}) restaura {@code estadoPrevio}
 *       (se respeta PENDIENTE). Los deshabilitados manualmente ({@code estadoPrevio = null})
 *       permanecen {@code DESHABILITADO}.</li>
 * </ul>
 *
 * <p>Los niveles hijos se actualizan en batch (interfaces {@code *BulkRepository} de este
 * paquete, métodos {@code @Modifying}) sin cargar entidades en memoria: el costo por acción
 * es constante (Empresa/Sucursal: 4 updates, Cámara: 2). Cada método es {@code @Transactional}
 * (REQUIRED: si ya vino de un service transaccional, se une a esa transacción). El nodo raíz
 * pasa *managed* si el llamador lo cargó en la misma transacción y sus cambios se persisten
 * por dirty checking.
 */
@Component
@RequiredArgsConstructor
@Transactional
public class CascadeStateService 
{
    private final SucursalBulkRepository sucursalBulkRepository;
    private final CamaraBulkRepository camaraBulkRepository;
    private final SensorBulkRepository sensorBulkRepository;
    private final UsuarioBulkRepository usuarioBulkRepository;

    // ===================== Empresa =====================

    public void eliminarEmpresa(Empresa empresa) 
    {
        Instant ahora = Instant.now();
        empresa.setDeletedAt(ahora);
        Long id = empresa.getId();
        sucursalBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, ahora);
        camaraBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, ahora);
        sensorBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, ahora);
        usuarioBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, ahora);
    }

    public void restaurarEmpresa(Empresa empresa) 
    {
        empresa.setDeletedAt(null);
        Long id = empresa.getId();
        sucursalBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, null);
        camaraBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, null);
        sensorBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, null);
        usuarioBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, null);
    }

    public void desactivarEmpresa(Empresa empresa) 
    {
        empresa.setActivo(false);
        Long id = empresa.getId();
        sucursalBulkRepository.bulkActualizarActivoPorEmpresa(id, false);
        camaraBulkRepository.bulkActualizarActivoPorEmpresa(id, false);
        sensorBulkRepository.bulkDeshabilitarPorEmpresa(id);
        usuarioBulkRepository.bulkActualizarActivoPorEmpresa(id, false);
    }

    public void activarEmpresa(Empresa empresa) 
    {
        empresa.setActivo(true);
        Long id = empresa.getId();
        sucursalBulkRepository.bulkActualizarActivoPorEmpresa(id, true);
        camaraBulkRepository.bulkActualizarActivoPorEmpresa(id, true);
        sensorBulkRepository.bulkActivarPorEmpresa(id);
        usuarioBulkRepository.bulkActualizarActivoPorEmpresa(id, true);
    }

    // ===================== Sucursal =====================

    public void eliminarSucursal(Sucursal sucursal) 
    {
        Instant ahora = Instant.now();
        sucursal.setDeletedAt(ahora);
        Long id = sucursal.getId();
        camaraBulkRepository.bulkActualizarDeletedAtPorSucursal(id, ahora);
        sensorBulkRepository.bulkActualizarDeletedAtPorSucursal(id, ahora);
        usuarioBulkRepository.bulkActualizarDeletedAtPorSucursal(id, ahora);
    }

    public void restaurarSucursal(Sucursal sucursal) 
    {
        sucursal.setDeletedAt(null);
        Long id = sucursal.getId();
        camaraBulkRepository.bulkActualizarDeletedAtPorSucursal(id, null);
        sensorBulkRepository.bulkActualizarDeletedAtPorSucursal(id, null);
        usuarioBulkRepository.bulkActualizarDeletedAtPorSucursal(id, null);
    }

    public void desactivarSucursal(Sucursal sucursal) 
    {
        sucursal.setActivo(false);
        Long id = sucursal.getId();
        camaraBulkRepository.bulkActualizarActivoPorSucursal(id, false);
        sensorBulkRepository.bulkDeshabilitarPorSucursal(id);
        usuarioBulkRepository.bulkActualizarActivoPorSucursal(id, false);
    }

    public void activarSucursal(Sucursal sucursal) 
    {
        sucursal.setActivo(true);
        Long id = sucursal.getId();
        camaraBulkRepository.bulkActualizarActivoPorSucursal(id, true);
        sensorBulkRepository.bulkActivarPorSucursal(id);
        usuarioBulkRepository.bulkActualizarActivoPorSucursal(id, true);
    }

    // ===================== Camara =====================

    public void eliminarCamara(Camara camara) 
    {
        Instant ahora = Instant.now();
        camara.setDeletedAt(ahora);
        sensorBulkRepository.bulkActualizarDeletedAtPorCamara(camara.getId(), ahora);
    }

    public void restaurarCamara(Camara camara) 
    {
        camara.setDeletedAt(null);
        sensorBulkRepository.bulkActualizarDeletedAtPorCamara(camara.getId(), null);
    }

    public void desactivarCamara(Camara camara) 
    {
        camara.setActivo(false);
        sensorBulkRepository.bulkDeshabilitarPorCamara(camara.getId());
    }

    public void activarCamara(Camara camara) 
    {
        camara.setActivo(true);
        sensorBulkRepository.bulkActivarPorCamara(camara.getId());
    }
}