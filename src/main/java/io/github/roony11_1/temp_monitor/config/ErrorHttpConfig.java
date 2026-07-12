package io.github.roony11_1.temp_monitor.config;

import io.github.roony11_1.error.rest.HttpStatusRegistry;
import io.github.roony11_1.temp_monitor.kernel.security.error.SecurityErrorCategories;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.CamaraErrorCategories;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.SensorErrorCategories;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.EmpresaErrorCategories;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.SucursalErrorCategories;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions.AuthErrorCategories;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions.UserErrorCategories;
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

        HttpStatusRegistry.register(AuthErrorCategories.INVALID_CREDENTIALS, 401);

        HttpStatusRegistry.register(UserErrorCategories.USER_NOT_FOUND, 404);
        HttpStatusRegistry.register(UserErrorCategories.USER_DISABLED, 403);
        HttpStatusRegistry.register(UserErrorCategories.EMAIL_ALREADY_EXISTS, 409);

        HttpStatusRegistry.register(EmpresaErrorCategories.EMPRESA_NOT_FOUND, 404);
        HttpStatusRegistry.register(EmpresaErrorCategories.EMPRESA_ALREADY_EXISTS, 409);

        HttpStatusRegistry.register(SucursalErrorCategories.SUCURSAL_NOT_FOUND, 404);

        HttpStatusRegistry.register(CamaraErrorCategories.CAMARA_NOT_FOUND, 404);

        HttpStatusRegistry.register(SensorErrorCategories.SENSOR_NOT_FOUND, 404);
        HttpStatusRegistry.register(SensorErrorCategories.SENSOR_ALREADY_EXISTS, 409);
        HttpStatusRegistry.register(SensorErrorCategories.SENSOR_DESHABILITADO, 423);
        HttpStatusRegistry.register(SensorErrorCategories.SENSOR_API_KEY_INVALIDA, 400);
    }
}
