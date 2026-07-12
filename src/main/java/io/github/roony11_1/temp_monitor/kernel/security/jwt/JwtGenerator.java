package io.github.roony11_1.temp_monitor.kernel.security.jwt;

import io.github.roony11_1.temp_monitor.config.JwtConfig;
import io.github.roony11_1.temp_monitor.kernel.security.exception.InvalidTokenUserException;
import io.github.roony11_1.temp_monitor.kernel.security.exception.JwtGenerationException;
import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtGenerator 
{
    private final JwtKeyProvider keyProvider;
    private final JwtConfig jwtConfig;

    public String generate(TokenUser user) 
    {
        Objects.requireNonNull(user.getId(), "El usuario no tiene ID");
        Objects.requireNonNull(user.getEmail(), "El usuario no tiene email");

        Set<String> roles = user.getRoles()
                .stream()
                .map(Rol::name)
                .collect(Collectors.toSet());

        if (roles.isEmpty()) 
        {
            throw new InvalidTokenUserException("El usuario no tiene roles asignados");
        }

        try 
        {
            SecretKey key = keyProvider.getHmacKey();

            var builder = Jwts.builder()
                    .issuer("temp-monitor")
                    .subject(user.getId().toString())
                    .claim("email", user.getEmail())
                    .claim("roles", roles)
                    .issuedAt(new Date())
                    .expiration(Date.from(Instant.now().plus(Duration.ofHours(jwtConfig.getExpirationHours()))));

            if (user.getEmpresaId() != null) 
            {
                builder.claim("empresaId", user.getEmpresaId());
            }

            if (user.getSucursalId() != null) 
            {
                builder.claim("sucursalId", user.getSucursalId());
            }

            return builder.signWith(key).compact();
        } 
        catch (Exception e) 
        {
            log.error("Error al generar JWT para userId={}", user.getId(), e);
            throw new JwtGenerationException(e.getMessage());
        }
    }
}
