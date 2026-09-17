package io.github.roony11_1.temp_monitor.kernel.cascade.handler;

import io.github.roony11_1.temp_monitor.kernel.cascade.SensorBulkRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CamaraCascadeHandler implements CascadeHandler<Camara> {

    private final SensorBulkRepository sensorBulkRepository;

    @Override
    public Class<Camara> supportedType() {
        return Camara.class;
    }

    @Override
    public void softDelete(Camara root, Instant now) {
        root.setDeletedAt(now);
        sensorBulkRepository.bulkActualizarDeletedAtPorCamara(root.getId(), now);
    }

    @Override
    public void restore(Camara root) {
        root.setDeletedAt(null);
        sensorBulkRepository.bulkActualizarDeletedAtPorCamara(root.getId(), null);
    }

    @Override
    public void deactivate(Camara root) {
        root.setActivo(false);
        sensorBulkRepository.bulkDeshabilitarPorCamara(root.getId());
    }

    @Override
    public void activate(Camara root) {
        root.setActivo(true);
        sensorBulkRepository.bulkActivarPorCamara(root.getId());
    }
}
