package io.github.roony11_1.temp_monitor.config.filter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.roony11_1.temp_monitor.modules.camara.core.application.SensorService;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.ApiKeyInvalidaException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorDeshabilitadoException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Sensor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensorApiKeyFilter extends OncePerRequestFilter
{
    private final SensorService sensorService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain) throws ServletException, IOException 
    {
        String apiKey = request.getHeader("X-API-KEY");
        String sensorUuidHeader = request.getHeader("X-Sensor-ID");

        if (apiKey == null || apiKey.isEmpty())
        {
            chain.doFilter(request, response);
            return;
        }

        if (sensorUuidHeader == null || sensorUuidHeader.isBlank()) 
        {
            throw new ApiKeyInvalidaException("X-Sensor-ID es requerido");
        }

        UUID sensorUuid = UUID.fromString(sensorUuidHeader.trim());

        Sensor sensor = sensorService.authenticateByUuidAndApiKey(
            sensorUuid,
            apiKey
        ).orElseThrow(() -> new ApiKeyInvalidaException("Credenciales inválidas"));

        switch (sensor.getEstado()) 
        {
            case PENDIENTE:
                log.info("Sensor pendiente de asignación: {}", sensor.getUuid());
                break;

            case ACTIVO:
                log.info("Sensor activo: {}", sensor.getUuid());
                break;

            case DESHABILITADO:
                log.warn("Sensor deshabilitado: {}", sensor.getUuid());
                throw new SensorDeshabilitadoException(sensor.getUuid().toString());

            default:
                log.error("Estado desconocido: {}", sensor.getEstado());
                throw new IllegalStateException("Estado de sensor no reconocido: " + sensor.getEstado());
        }

        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(
                sensor.getUuid().toString(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_SENSOR")));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException 
    {
        String path = request.getRequestURI();
        return path.equals("/api/sensores/registrar")
            || path.startsWith("/auth/")
            || path.startsWith("/actuator/");
    }
}
