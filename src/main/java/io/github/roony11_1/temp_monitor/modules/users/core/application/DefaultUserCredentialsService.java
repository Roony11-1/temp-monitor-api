package io.github.roony11_1.temp_monitor.modules.users.core.application;

import io.github.roony11_1.temp_monitor.kernel.security.crypto.HashService;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.kernel.security.service.IUserCredentialsService;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions.InvalidCredentialsException;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions.UserDisabledException;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DefaultUserCredentialsService implements IUserCredentialsService 
{
    private final UsuarioRepository usuarioRepository;
    private final HashService passwordHasher;

    @Override
    @Transactional
    public TokenUser authenticate(String email, String rawPassword) 
    {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null || !passwordHasher.verify(rawPassword, usuario.getPasswordHash())) 
        {
            throw new InvalidCredentialsException();
        }

        if (!usuario.isActivo()) 
        {
            throw new UserDisabledException();
        }

        usuario.setLastLogin(Instant.now());

        return usuario;
    }
}
