package io.github.roony11_1.temp_monitor.modules.auth.core.application;

import io.github.roony11_1.temp_monitor.kernel.security.exception.JwtGenerationException;
import io.github.roony11_1.temp_monitor.kernel.security.jwt.JwtGenerator;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.kernel.security.service.IUserCredentialsService;
import io.github.roony11_1.temp_monitor.modules.auth.api.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService 
{
    private final IUserCredentialsService userCredentialsService;
    private final JwtGenerator jwtGenerator;

    public String login(LoginRequest request) 
    {
        var email = request.getEmail();
        var password = request.getPassword();

        log.info("Intento de login para email: {}", email);

        TokenUser user = userCredentialsService.authenticate(email, password);

        try 
        {
            String token = jwtGenerator.generate(user);
            log.info("Login exitoso para email: {}", email);
            return token;
        } catch (Exception e) 
        {
            log.error("Error al generar JWT para email={}", email, e);
            throw new JwtGenerationException(e.getMessage());
        }
    }
}
