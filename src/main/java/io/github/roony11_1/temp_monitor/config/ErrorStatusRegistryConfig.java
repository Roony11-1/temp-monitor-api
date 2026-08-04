package io.github.roony11_1.temp_monitor.config;

import io.github.roony11_1.error.rest.HttpStatusRegistry;
import io.github.roony11_1.temp_monitor.kernel.security.error.SecurityErrorCategories;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorStatusRegistryConfig 
{
    @PostConstruct
    public void registrarStatus() 
    {
        HttpStatusRegistry.register(SecurityErrorCategories.ACCESS_DENIED, 403);
        HttpStatusRegistry.register(SecurityErrorCategories.AUTHENTICATION_REQUIRED, 401);
    }
}