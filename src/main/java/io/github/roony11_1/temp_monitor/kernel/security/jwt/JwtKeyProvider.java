package io.github.roony11_1.temp_monitor.kernel.security.jwt;

import io.github.roony11_1.temp_monitor.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwtKeyProvider 
{
    private final JwtConfig jwtConfig;

    public SecretKey getHmacKey() 
    {
        byte[] decoded = Base64.getDecoder().decode(jwtConfig.getSecret());
        return new SecretKeySpec(decoded, "HmacSHA256");
    }
}
