package io.github.roony11_1.temp_monitor.kernel.cascade;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.SensorRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.SucursalRepository;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
 *       {@code estado=DESHABILITADO}.</li>
 *   <li>{@code activar*}: {@code activo=true} en el nodo y sus hijos; para sensores
 *       con {@code estado=DESHABILITADO}, {@code estado=ACTIVO} (se respeta PENDIENTE).</li>
 * </ul>
 *
 * <p>Los niveles hijos se actualizan en batch (métodos {@code @Modifying} de los
 * repositorios), sin cargar entidades en memoria: el costo por acción es constante
 * (Empresa/Sucursal: 4 updates, Cámara: 2). Debe ejecutarse dentro de una transacción
 * (los services que lo invocan son {@code @Transactional}); el nodo raíz queda *managed*
 * y sus cambios se persisten por dirty checking.
 */
@Component
@RequiredArgsConstructor
public class CascadeStateService 
{
    private final SucursalRepository sucursalRepository;
    private final CamaraRepository camaraRepository;
    private final SensorRepository sensorRepository;
    private final UsuarioRepository usuarioRepository;

    // ===================== Empresa =====================

    public void eliminarEmpresa(Empresa empresa) 
    {
        Instant ahora = Instant.now();
        empresa.setDeletedAt(ahora);
        Long id = empresa.getId();
        sucursalRepository.bulkActualizarDeletedAtPorEmpresa(id, ahora);
        camaraRepository.bulkActualizarDeletedAtPorEmpresa(id, ahora);
        sensorRepository.bulkActualizarDeletedAtPorEmpresa(id, ahora);
        usuarioRepository.bulkActualizarDeletedAtPorEmpresa(id, ahora);
    }

    public void restaurarEmpresa(Empresa empresa) 
    {
        empresa.setDeletedAt(null);
        Long id = empresa.getId();
        sucursalRepository.bulkActualizarDeletedAtPorEmpresa(id, null);
        camaraRepository.bulkActualizarDeletedAtPorEmpresa(id, null);
        sensorRepository.bulkActualizarDeletedAtPorEmpresa(id, null);
        usuarioRepository.bulkActualizarDeletedAtPorEmpresa(id, null);
    }

    public void desactivarEmpresa(Empresa empresa) 
    {
        empresa.setActivo(false);
        Long id = empresa.getId();
        sucursalRepository.bulkActualizarActivoPorEmpresa(id, false);
        camaraRepository.bulkActualizarActivoPorEmpresa(id, false);
        sensorRepository.bulkActualizarEstadoPorEmpresa(id, EstadoSensor.DESHABILITADO, null);
        usuarioRepository.bulkActualizarActivoPorEmpresa(id, false);
    }

    public void activarEmpresa(Empresa empresa) 
    {
        empresa.setActivo(true);
        Long id = empresa.getId();
        sucursalRepository.bulkActualizarActivoPorEmpresa(id, true);
        camaraRepository.bulkActualizarActivoPorEmpresa(id, true);
        sensorRepository.bulkActualizarEstadoPorEmpresa(id, EstadoSensor.ACTIVO, EstadoSensor.DESHABILITADO);
        usuarioRepository.bulkActualizarActivoPorEmpresa(id, true);
    }

    // ===================== Sucursal =====================

    public void eliminarSucursal(Sucursal sucursal) 
    {
        Instant ahora = Instant.now();
        sucursal.setDeletedAt(ahora);
        Long id = sucursal.getId();
        camaraRepository.bulkActualizarDeletedAtPorSucursal(id, ahora);
        sensorRepository.bulkActualizarDeletedAtPorSucursal(id, ahora);
        usuarioRepository.bulkActualizarDeletedAtPorSucursal(id, ahora);
    }

    public void restaurarSucursal(Sucursal sucursal) 
    {
        sucursal.setDeletedAt(null);
        Long id = sucursal.getId();
        camaraRepository.bulkActualizarDeletedAtPorSucursal(id, null);
        sensorRepository.bulkActualizarDeletedAtPorSucursal(id, null);
        usuarioRepository.bulkActualizarDeletedAtPorSucursal(id, null);
    }

    public void desactivarSucursal(Sucursal sucursal) 
    {
        sucursal.setActivo(false);
        Long id = sucursal.getId();
        camaraRepository.bulkActualizarActivoPorSucursal(id, false);
        sensorRepository.bulkActualizarEstadoPorSucursal(id, EstadoSensor.DESHABILITADO, null);
        usuarioRepository.bulkActualizarActivoPorSucursal(id, false);
    }

    public void activarSucursal(Sucursal sucursal) 
    {
        sucursal.setActivo(true);
        Long id = sucursal.getId();
        camaraRepository.bulkActualizarActivoPorSucursal(id, true);
        sensorRepository.bulkActualizarEstadoPorSucursal(id, EstadoSensor.ACTIVO, EstadoSensor.DESHABILITADO);
        usuarioRepository.bulkActualizarActivoPorSucursal(id, true);
    }

    // ===================== Camara =====================

    public void eliminarCamara(Camara camara) 
    {
        Instant ahora = Instant.now();
        camara.setDeletedAt(ahora);
        sensorRepository.bulkActualizarDeletedAtPorCamara(camara.getId(), ahora);
    }

    public void restaurarCamara(Camara camara) 
    {
        camara.setDeletedAt(null);
        sensorRepository.bulkActualizarDeletedAtPorCamara(camara.getId(), null);
    }

    public void desactivarCamara(Camara camara) 
    {
        camara.setActivo(false);
        sensorRepository.bulkActualizarEstadoPorCamara(camara.getId(), EstadoSensor.DESHABILITADO, null);
    }

    public void activarCamara(Camara camara) 
    {
        camara.setActivo(true);
        sensorRepository.bulkActualizarEstadoPorCamara(camara.getId(), EstadoSensor.ACTIVO, EstadoSensor.DESHABILITADO);
    }
}