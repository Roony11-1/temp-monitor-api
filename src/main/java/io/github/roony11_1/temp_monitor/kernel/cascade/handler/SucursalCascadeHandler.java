package io.github.roony11_1.temp_monitor.kernel.cascade.handler;

import io.github.roony11_1.temp_monitor.kernel.cascade.CamaraBulkRepository;
import io.github.roony11_1.temp_monitor.kernel.cascade.SensorBulkRepository;
import io.github.roony11_1.temp_monitor.kernel.cascade.UsuarioBulkRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class SucursalCascadeHandler implements CascadeHandler<Sucursal> {

    private final CamaraBulkRepository camaraBulkRepository;
    private final SensorBulkRepository sensorBulkRepository;
    private final UsuarioBulkRepository usuarioBulkRepository;

    @Override
    public Class<Sucursal> supportedType() {
        return Sucursal.class;
    }

    @Override
    public void softDelete(Sucursal root, Instant now) {
        root.setDeletedAt(now);
        Long id = root.getId();
        camaraBulkRepository.bulkActualizarDeletedAtPorSucursal(id, now);
        sensorBulkRepository.bulkActualizarDeletedAtPorSucursal(id, now);
        usuarioBulkRepository.bulkActualizarDeletedAtPorSucursal(id, now);
    }

    @Override
    public void restore(Sucursal root) {
        root.setDeletedAt(null);
        Long id = root.getId();
        camaraBulkRepository.bulkActualizarDeletedAtPorSucursal(id, null);
        sensorBulkRepository.bulkActualizarDeletedAtPorSucursal(id, null);
        usuarioBulkRepository.bulkActualizarDeletedAtPorSucursal(id, null);
    }

    @Override
    public void deactivate(Sucursal root) {
        root.setActivo(false);
        Long id = root.getId();
        camaraBulkRepository.bulkActualizarActivoPorSucursal(id, false);
        sensorBulkRepository.bulkDeshabilitarPorSucursal(id);
        usuarioBulkRepository.bulkActualizarActivoPorSucursal(id, false);
    }

    @Override
    public void activate(Sucursal root) {
        root.setActivo(true);
        Long id = root.getId();
        camaraBulkRepository.bulkActualizarActivoPorSucursal(id, true);
        sensorBulkRepository.bulkActivarPorSucursal(id);
        usuarioBulkRepository.bulkActualizarActivoPorSucursal(id, true);
    }
}
