package io.github.roony11_1.temp_monitor.modules.users.core.application;

import io.github.roony11_1.temp_monitor.kernel.specification.FilterSpecification;
import io.github.roony11_1.temp_monitor.kernel.security.crypto.HashService;
import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.EmpresaRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.SucursalRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.EmpresaNotFoundException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.SucursalNotFoundException;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioRequest;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions.EmailAlreadyExistsException;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioResponse;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions.UserNotFoundException;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsuarioService 
{
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;
    private final HashService passwordHasher;

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() 
    {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Usuario> listarTodos(Pageable pageable) 
    {
        return usuarioRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarTodos(Pageable pageable, Map<String, String> filters)
    {
        Page<Usuario> page;

        if (filters == null || filters.isEmpty()) {
            page = usuarioRepository.findAll(pageable);
        } else {
            var spec = FilterSpecification.<Usuario>from(filters);
            page = usuarioRepository.findAll(spec, pageable);
        }

        return page.map(UsuarioResponse::toResponse);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarPorEmpresa(Long empresaId) 
    {
        return usuarioRepository.findByEmpresa_Id(empresaId);
    }

    @Transactional(readOnly = true)
    public Page<Usuario> listarPorEmpresa(Long empresaId, Pageable pageable) 
    {
        return usuarioRepository.findByEmpresa_Id(empresaId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarPorSucursal(Long sucursalId) 
    {
        return usuarioRepository.findBySucursal_Id(sucursalId);
    }

    @Transactional(readOnly = true)
    public Page<Usuario> listarPorSucursal(Long sucursalId, Pageable pageable) 
    {
        return usuarioRepository.findBySucursal_Id(sucursalId, pageable);
    }

    @Transactional(readOnly = true)
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

        Empresa empresa = null;
        if (request.getEmpresaId() != null) {
            empresa = empresaRepository.findById(request.getEmpresaId())
                    .orElseThrow(() -> new EmpresaNotFoundException("ID " + request.getEmpresaId()));
        }

        Sucursal sucursal = null;
        if (request.getSucursalId() != null) {
            sucursal = sucursalRepository.findById(request.getSucursalId())
                    .orElseThrow(() -> new SucursalNotFoundException("ID " + request.getSucursalId()));
        }

        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .passwordHash(passwordHasher.hash(request.getPassword()))
                .nombre(request.getNombre())
                .roles(new HashSet<>(request.getRoles()))
                .empresa(empresa)
                .sucursal(sucursal)
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

        if (request.getEmpresaId() != null) 
        {
            Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                    .orElseThrow(() -> new EmpresaNotFoundException("ID " + request.getEmpresaId()));

            usuario.setEmpresa(empresa);
        } 
        else 
        {
            usuario.setEmpresa(null);
        }

        if (request.getSucursalId() != null) 
        {
            Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                    .orElseThrow(() -> new SucursalNotFoundException("ID " + request.getSucursalId()));
            usuario.setSucursal(sucursal);
        } 
        else 
        {
            usuario.setSucursal(null);
        }
        
        if (request.getRoles() != null && !request.getRoles().isEmpty()) 
        {
            usuario.setRoles(request.getRoles());
        }

        return usuario;
    }

    @Transactional
    public void cambiarPassword(Long id, String nuevaPassword) 
    {
        Usuario usuario = buscarPorId(id);
        usuario.setPasswordHash(passwordHasher.hash(nuevaPassword));
    }

    @Transactional
    public void activar(Long id) 
    {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(true);
    }

    @Transactional
    public void desactivar(Long id) 
    {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
    }

    @Transactional
    public void eliminar(Long id) 
    {
        var usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }
}
