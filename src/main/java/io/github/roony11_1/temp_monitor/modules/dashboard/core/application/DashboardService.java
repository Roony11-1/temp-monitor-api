package io.github.roony11_1.temp_monitor.modules.dashboard.core.application;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
        Instant since = Instant.now().minus(Duration.ofHours(24));
        List<io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Lectura> lecturas = lecturaRepository.findUltimas24h(since);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm").withZone(ZoneId.systemDefault());

        return lecturas.stream()
            .map(l -> new TemperaturePoint(fmt.format(l.getTimestamp()), l.getTemperatura()))
            .toList();
    }
}
