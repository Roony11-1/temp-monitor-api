package io.github.roony11_1.temp_monitor.modules.auth.core.application;

import io.github.roony11_1.error.core.exceptions.InternalErrorException;
import io.github.roony11_1.temp_monitor.kernel.security.jwt.JwtGenerator;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.kernel.security.service.IUserCredentialsService;
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

    public String login(String email, String password) 
    {
        log.info("Intento de login para email: {}", email);

        TokenUser user = userCredentialsService.authenticate(email, password);

        try 
        {
            String token = jwtGenerator.generate(user);
            log.info("Login exitoso para email: {}", email);
            return token;
        } catch (Exception e) 
        {
            throw new InternalErrorException("generar el token JWT", e);
        }
    }
}
