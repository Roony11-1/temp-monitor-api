package io.github.roony11_1.temp_monitor.modules.dashboard.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardResponse
{
    private long empresas;
    private long sucursales;
    private long camarasActivas;
    private long sensoresOnline;
    private long sensoresOffline;
    private double temperaturaPromedio;
    private List<TemperaturePoint> temperatura24h;

    @Data
    @AllArgsConstructor
    public static class TemperaturePoint
    {
        private String hora;
        private double temperatura;
    }
}
