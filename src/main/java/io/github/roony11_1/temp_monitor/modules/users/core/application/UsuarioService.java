package io.github.roony11_1.temp_monitor.modules.users.core.application;

import io.github.roony11_1.temp_monitor.kernel.security.crypto.HashService;
import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioRequest;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions.EmailAlreadyExistsException;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions.UserNotFoundException;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService 
{
    private final UsuarioRepository usuarioRepository;
    private final HashService passwordHasher;

    public List<Usuario> listarTodos() 
    {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarPorEmpresa(Long empresaId) 
    {
        return usuarioRepository.findByEmpresaId(empresaId);
    }

    public List<Usuario> listarPorSucursal(Long sucursalId) 
    {
        return usuarioRepository.findBySucursalId(sucursalId);
    }

    public Usuario buscarPorId(Long id) 
    {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("ID " + id));
    }

    @Transactional
    public Usuario crear(UsuarioRequest request) 
    {
        if (usuarioRepository.existsByEmail(request.getEmail()))
            throw new EmailAlreadyExistsException(request.getEmail());

        TokenUser currentUser = getCurrentUser();

        if (currentUser.getRoles().contains(Rol.SUPER_ADMIN)) 
        {
            // SUPER_ADMIN puede crear cualquier rol
        } 
        else if (currentUser.getRoles().contains(Rol.ADMIN_EMPRESA)) 
        {
            // ADMIN_EMPRESA solo puede crear ADMIN_SUCURSAL, TECNICO, USUARIO
            for (Rol r : request.getRoles()) 
            {
                if (r == Rol.SUPER_ADMIN || r == Rol.ADMIN_EMPRESA) 
                {
                    throw new AccessDeniedException("No puedes crear usuarios con rol " + r);
                }
            }
            // Debe asignar su misma empresa
            if (request.getEmpresaId() == null || !request.getEmpresaId().equals(currentUser.getEmpresaId())) 
            {
                throw new AccessDeniedException("Solo puedes crear usuarios en tu propia empresa");
            }
        } 
        else 
        {
            throw new AccessDeniedException("No tienes permiso para crear usuarios");
        }

        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .passwordHash(passwordHasher.hash(request.getPassword()))
                .nombre(request.getNombre())
                .roles(new HashSet<>(request.getRoles()))
                .empresaId(request.getEmpresaId())
                .sucursalId(request.getSucursalId())
                .activo(true)
                .build();

        return usuarioRepository.save(usuario);
    }

    private TokenUser getCurrentUser() 
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof TokenUser)) 
        {
            throw new AccessDeniedException("Usuario no autenticado");
        }
        return (TokenUser) auth.getPrincipal();
    }

    @Transactional
    public Usuario actualizar(Long id, UsuarioRequest request) 
    {
        Usuario usuario = buscarPorId(id);

        usuario.setNombre(request.getNombre());
        usuario.setTelefono(request.getTelefono());
        usuario.setEmpresaId(request.getEmpresaId());
        usuario.setSucursalId(request.getSucursalId());
        
        if (request.getRoles() != null && !request.getRoles().isEmpty()) 
        {
            usuario.setRoles(request.getRoles());
        }

        usuario.setUpdatedAt(Instant.now());
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarPassword(Long id, String nuevaPassword) 
    {
        Usuario usuario = buscarPorId(id);
        usuario.setPasswordHash(passwordHasher.hash(nuevaPassword));
        usuario.setUpdatedAt(Instant.now());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void activar(Long id) 
    {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(true);
        usuario.setUpdatedAt(Instant.now());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void desactivar(Long id) 
    {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuario.setUpdatedAt(Instant.now());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Long id) 
    {
        var usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }
}
