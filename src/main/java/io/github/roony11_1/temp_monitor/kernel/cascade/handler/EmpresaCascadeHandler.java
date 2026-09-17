package io.github.roony11_1.temp_monitor.kernel.cascade.handler;

import io.github.roony11_1.temp_monitor.kernel.cascade.CamaraBulkRepository;
import io.github.roony11_1.temp_monitor.kernel.cascade.SensorBulkRepository;
import io.github.roony11_1.temp_monitor.kernel.cascade.SucursalBulkRepository;
import io.github.roony11_1.temp_monitor.kernel.cascade.UsuarioBulkRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class EmpresaCascadeHandler implements CascadeHandler<Empresa> {

    private final SucursalBulkRepository sucursalBulkRepository;
    private final CamaraBulkRepository camaraBulkRepository;
    private final SensorBulkRepository sensorBulkRepository;
    private final UsuarioBulkRepository usuarioBulkRepository;

    @Override
    public Class<Empresa> supportedType() {
        return Empresa.class;
    }

    @Override
    public void softDelete(Empresa root, Instant now) {
        root.setDeletedAt(now);
        Long id = root.getId();
        sucursalBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, now);
        camaraBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, now);
        sensorBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, now);
        usuarioBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, now);
    }

    @Override
    public void restore(Empresa root) {
        root.setDeletedAt(null);
        Long id = root.getId();
        sucursalBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, null);
        camaraBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, null);
        sensorBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, null);
        usuarioBulkRepository.bulkActualizarDeletedAtPorEmpresa(id, null);
    }

    @Override
    public void deactivate(Empresa root) {
        root.setActivo(false);
        Long id = root.getId();
        sucursalBulkRepository.bulkActualizarActivoPorEmpresa(id, false);
        camaraBulkRepository.bulkActualizarActivoPorEmpresa(id, false);
        sensorBulkRepository.bulkDeshabilitarPorEmpresa(id);
        usuarioBulkRepository.bulkActualizarActivoPorEmpresa(id, false);
    }

    @Override
    public void activate(Empresa root) {
        root.setActivo(true);
        Long id = root.getId();
        sucursalBulkRepository.bulkActualizarActivoPorEmpresa(id, true);
        camaraBulkRepository.bulkActualizarActivoPorEmpresa(id, true);
        sensorBulkRepository.bulkActivarPorEmpresa(id);
        usuarioBulkRepository.bulkActualizarActivoPorEmpresa(id, true);
    }
}
