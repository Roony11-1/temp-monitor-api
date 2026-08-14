package io.github.roony11_1.temp_monitor.modules.auth.core.application;

import io.github.roony11_1.temp_monitor.config.JwtConfig;
import io.github.roony11_1.temp_monitor.kernel.security.exception.NoAutenticadoException;
import io.github.roony11_1.temp_monitor.modules.auth.core.domain.model.RefreshToken;
import io.github.roony11_1.temp_monitor.modules.auth.core.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Emite, valida y revoca refresh tokens (opacos, rotativos).
 *
 * <p>El token en claro (UUID aleatorio) se entrega una sola vez; en BD solo
 * queda su hash SHA-256. Cada renovación usa rotación (se revoca el token
 * usado) y el logout revoca el token activo del usuario.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService 
{
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfig jwtConfig;

    @Transactional
    public String issue(Long userId) 
    {
        String raw = UUID.randomUUID().toString();
        RefreshToken token = RefreshToken.builder()
                .tokenHash(hash(raw))
                .userId(userId)
                .expiresAt(Instant.now().plus(Duration.ofDays(jwtConfig.getRefreshExpirationDays())))
                .build();
        refreshTokenRepository.save(token);
        return raw;
    }

    @Transactional(readOnly = true)
    public Long validate(String raw) 
    {
        return findValid(raw).getUserId();
    }

    @Transactional
    public void revoke(String raw) 
    {
        findValid(raw).setRevokedAt(Instant.now());
    }

    private RefreshToken findValid(String raw) 
    {
        if (raw == null || raw.isBlank()) 
        {
            throw new NoAutenticadoException("Refresh token inválido");
        }

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash(raw))
                .orElseThrow(() -> new NoAutenticadoException("Refresh token inválido"));

        if (token.isRevoked()) 
        {
            throw new NoAutenticadoException("Refresh token revocado");
        }

        if (token.isExpired(Instant.now())) 
        {
            throw new NoAutenticadoException("Refresh token expirado");
        }

        return token;
    }

    private String hash(String raw) 
    {
        try 
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } 
        catch (NoSuchAlgorithmException e) 
        {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}