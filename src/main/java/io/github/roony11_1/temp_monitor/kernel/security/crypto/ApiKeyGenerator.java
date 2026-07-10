package io.github.roony11_1.temp_monitor.kernel.security.crypto;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class ApiKeyGenerator 
{
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generate()
    {
        byte[] bytes = new byte[32];

        RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);
    }
}
