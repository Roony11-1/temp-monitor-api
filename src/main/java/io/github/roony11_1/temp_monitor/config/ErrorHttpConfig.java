package io.github.roony11_1.temp_monitor.config;

import io.github.roony11_1.error.rest.HttpStatusRegistry;
import io.github.roony11_1.temp_monitor.kernel.security.error.SecurityErrorCategories;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorErrorCategories;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorHttpConfig 
{
    @PostConstruct
    void registerCustomMappings() 
    {
        HttpStatusRegistry.register(SecurityErrorCategories.JWT_GENERATION_FAILED, 503);
        HttpStatusRegistry.register(SecurityErrorCategories.INVALID_TOKEN_USER, 400);

        HttpStatusRegistry.register(SensorErrorCategories.SENSOR_DESHABILITADO, 423);
        HttpStatusRegistry.register(SensorErrorCategories.SENSOR_SIN_CAMARA, 423);
    }
}