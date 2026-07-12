package io.github.roony11_1.temp_monitor.modules.dashboard.api.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.roony11_1.temp_monitor.modules.dashboard.api.dto.DashboardResponse;
import io.github.roony11_1.temp_monitor.modules.dashboard.core.application.DashboardService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController
{
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> obtenerDashboard()
    {
        return ResponseEntity.ok(dashboardService.obtenerDashboard());
    }
}
