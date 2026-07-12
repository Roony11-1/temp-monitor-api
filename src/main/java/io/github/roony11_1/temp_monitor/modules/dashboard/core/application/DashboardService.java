package io.github.roony11_1.temp_monitor.modules.dashboard.core.application;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Lectura;
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
        Instant desde = Instant.now().minus(Duration.ofHours(24));
        List<Lectura> lecturas = lecturaRepository.findByTimestampAfterOrderByTimestampAsc(desde);

        Map<String, List<Double>> agrupadas = lecturas.stream()
            .collect(Collectors.groupingBy(
                l -> {
                    LocalDateTime dt = LocalDateTime.ofInstant(l.getTimestamp(), ZoneId.systemDefault());
                    return dt.getHour() + ":00";
                },
                Collectors.mapping(Lectura::getTemperatura, Collectors.toList())
            ));

        List<TemperaturePoint> puntos = new ArrayList<>();
        int horaActual = LocalDateTime.now().getHour();
        for (int i = 23; i >= 0; i--)
        {
            int hora = (horaActual - i + 24) % 24;
            String label = String.format("%02d:00", hora);
            List<Double> temps = agrupadas.get(label);
            double promedio = temps != null && !temps.isEmpty()
                ? temps.stream().mapToDouble(Double::doubleValue).average().orElse(0.0)
                : 0.0;
            puntos.add(new TemperaturePoint(label, Math.round(promedio * 10.0) / 10.0));
        }

        return puntos;
    }
}
