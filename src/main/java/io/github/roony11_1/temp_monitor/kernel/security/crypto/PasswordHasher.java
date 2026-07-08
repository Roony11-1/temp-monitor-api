package io.github.roony11_1.temp_monitor.kernel.security.crypto;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordHasher 
{
    private final PasswordEncoder passwordEncoder;

    public String hash(String password) 
    {
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("Password no puede ser null o vacío");
        return passwordEncoder.encode(password);
    }

    public boolean verify(String password, String hash) 
    {
        if (password == null || hash == null)
            return false;
        return passwordEncoder.matches(password, hash);
    }
}

