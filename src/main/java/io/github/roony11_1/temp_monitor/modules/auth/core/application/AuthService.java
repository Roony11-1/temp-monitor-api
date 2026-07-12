package io.github.roony11_1.temp_monitor.modules.auth.core.application;

import io.github.roony11_1.temp_monitor.kernel.security.exception.JwtGenerationException;
import io.github.roony11_1.temp_monitor.kernel.security.jwt.JwtGenerator;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.kernel.security.service.IUserCredentialsService;
import io.github.roony11_1.temp_monitor.modules.auth.api.dto.LoginRequest;
import io.github.roony11_1.temp_monitor.modules.auth.api.dto.LoginResponse;
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

    public LoginResponse login(LoginRequest request) 
    {
        var email = request.getEmail();
        var password = request.getPassword();
        
        TokenUser user = userCredentialsService.authenticate(email, password);

        try 
        {
            String token = jwtGenerator.generate(user);

            return LoginResponse.builder()
                .token(token)
                .build();
        } 
        catch (Exception e) 
        {
            throw new JwtGenerationException(e.getMessage());
        }
    }
}
