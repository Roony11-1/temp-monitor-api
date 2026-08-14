package io.github.roony11_1.temp_monitor.modules.auth.core.application;

import io.github.roony11_1.temp_monitor.kernel.security.exception.JwtGenerationException;
import io.github.roony11_1.temp_monitor.kernel.security.exception.NoAutenticadoException;
import io.github.roony11_1.temp_monitor.kernel.security.jwt.JwtGenerator;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.kernel.security.service.IUserCredentialsService;
import io.github.roony11_1.temp_monitor.modules.auth.api.dto.LoginRequest;
import io.github.roony11_1.temp_monitor.modules.auth.api.dto.LoginResponse;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions.UserDisabledException;
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
    private final RefreshTokenService refreshTokenService;

    public LoginResponse login(LoginRequest request) 
    {
        TokenUser user = userCredentialsService.authenticate(request.getEmail(), request.getPassword());
        return buildTokens(user);
    }

    /**
     * Renueva el par de tokens usando el refresh (rotación). Revalida contra
     * BD que el usuario y su empresa/sucursal sigan activos; si no, revoca
     * el refresh usado y rechaza la renovación.
     */
    public LoginResponse refresh(String rawRefreshToken) 
    {
        Long userId = refreshTokenService.validate(rawRefreshToken);

        try 
        {
            TokenUser user = userCredentialsService.validateAndGetByUserId(userId);
            refreshTokenService.revoke(rawRefreshToken);
            return buildTokens(user);
        } 
        catch (UserDisabledException | NoAutenticadoException e) 
        {
            refreshTokenService.revoke(rawRefreshToken);
            throw e;
        }
    }

    public void logout(String rawRefreshToken) 
    {
        refreshTokenService.revoke(rawRefreshToken);
    }

    private LoginResponse buildTokens(TokenUser user) 
    {
        try 
        {
            String token = jwtGenerator.generate(user);
            String refreshToken = refreshTokenService.issue(user.id());

            return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
        } 
        catch (Exception e) 
        {
            throw new JwtGenerationException(e.getMessage());
        }
    }
}