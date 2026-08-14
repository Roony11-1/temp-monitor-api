package io.github.roony11_1.temp_monitor.modules.users.core.application;

import io.github.roony11_1.temp_monitor.kernel.security.crypto.HashService;
import io.github.roony11_1.temp_monitor.kernel.security.exception.NoAutenticadoException;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.kernel.security.service.IUserCredentialsService;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
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

        validarEmpresaYSucursalActivas(usuario);

        usuario.setLastLogin(Instant.now());

        return new TokenUser(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRoles(),
                usuario.getEmpresaId(),
                usuario.getSucursalId());
    }

    @Override
    @Transactional(readOnly = true)
    public TokenUser validateAndGetByUserId(Long userId)
    {
        // findById excluye usuarios soft-deleted (deletedAt IS NULL)
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new NoAutenticadoException("Usuario no encontrado o eliminado"));

        if (!usuario.isActivo())
        {
            throw new UserDisabledException();
        }

        validarEmpresaYSucursalActivas(usuario);

        return new TokenUser(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRoles(),
                usuario.getEmpresaId(),
                usuario.getSucursalId());
    }

    /**
     * Un usuario no puede iniciar sesión si su empresa o su sucursal está
     * eliminada o desactivada (la cascada de estado propaga ese cambio hacia abajo).
     */
    private void validarEmpresaYSucursalActivas(Usuario usuario)
    {
        Empresa empresa = usuario.getEmpresa();
        if (empresa != null && (empresa.getDeletedAt() != null || !empresa.isActivo()))
        {
            throw new UserDisabledException();
        }

        Sucursal sucursal = usuario.getSucursal();
        if (sucursal != null && (sucursal.getDeletedAt() != null || !sucursal.isActivo()))
        {
            throw new UserDisabledException();
        }
    }
}
