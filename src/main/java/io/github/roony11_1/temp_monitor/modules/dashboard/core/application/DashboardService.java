package io.github.roony11_1.temp_monitor.modules.dashboard.core.application;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.LecturaRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.SensorRepository;
import io.github.roony11_1.temp_monitor.modules.dashboard.api.dto.DashboardResponse;
import io.github.roony11_1.temp_monitor.modules.dashboard.api.dto.DashboardResponse.TemperaturePoint;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.EmpresaRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService
{
    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;
    private final CamaraRepository camaraRepository;
    private final SensorRepository sensorRepository;
    private final LecturaRepository lecturaRepository;

    @Transactional(readOnly = true)
    public DashboardResponse obtenerDashboard()
    {
        Instant threshold = Instant.now().minus(Duration.ofMinutes(5));

        long empresas = empresaRepository.count();
        long sucursales = sucursalRepository.count();
        long camarasActivas = camaraRepository.countByActivo(true);
        long sensoresOnline = sensorRepository.countOnline(threshold);
        long sensoresOffline = sensorRepository.countOffline(threshold);

        List<TemperaturePoint> temperatura24h = obtenerTemperatura24h();
        double temperaturaPromedio = temperatura24h.stream()
            .mapToDouble(TemperaturePoint::getTemperatura)
            .average()
            .orElse(0.0);

        return new DashboardResponse(
            empresas,
            sucursales,
            camarasActivas,
            sensoresOnline,
            sensoresOffline,
            Math.round(temperaturaPromedio * 10.0) / 10.0,
            temperatura24h
        );
    }

    private List<TemperaturePoint> obtenerTemperatura24h()
    {
        List<TemperaturePoint> puntos = new ArrayList<>();

        return puntos;
    }
}
