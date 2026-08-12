package io.github.roony11_1.temp_monitor.kernel.security.jwt;

import io.github.roony11_1.temp_monitor.config.JwtConfig;
import io.github.roony11_1.temp_monitor.kernel.security.exception.InvalidTokenUserException;
import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider 
{
    private final JwtConfig jwtConfig;

    private SecretKey getSigningKey() 
    {
        byte[] keyBytes = Base64.getDecoder().decode(jwtConfig.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token)
    {
        try 
        {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } 
        catch (Exception e) 
        {
            return false;
        }
    }

    private Claims getClaims(String token) 
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token) 
    {
        return Long.valueOf(getClaims(token).getSubject());
    }

    public String getEmail(String token) 
    {
        return getClaims(token).get("email", String.class);
    }

    public List<String> getRoles(String token) 
    {
        return getClaims(token).get("roles", List.class);
    }

    public TokenUser getTokenUser(String token) 
    {
        Claims claims = getClaims(token);
        Long userId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);
        List<String> roleNames = claims.get("roles", List.class);
        if (roleNames == null || roleNames.isEmpty()) 
        {
            throw new InvalidTokenUserException("El token no contiene roles");
        }
        Set<Rol> roles = roleNames.stream()
                .map(Rol::valueOf)
                .collect(Collectors.toSet());
        Long empresaId = claims.get("empresaId", Long.class);
        Long sucursalId = claims.get("sucursalId", Long.class);

        return new TokenUser(userId, email, roles, empresaId, sucursalId);
    }
}
