package io.github.roony11_1.temp_monitor.modules.users.core.application;

import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.kernel.security.exception.AccesoDenegadoException;
import io.github.roony11_1.temp_monitor.kernel.security.exception.NoAutenticadoException;
import io.github.roony11_1.temp_monitor.kernel.security.scope.CurrentUserScope;
import io.github.roony11_1.specification.core.FilterCondition;
import io.github.roony11_1.specification.core.FilterOperator;
import io.github.roony11_1.specification.spring.FilterSpecificationBuilder;
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
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions.EmailAlreadyExistsException;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.exceptions.UserNotFoundException;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService 
{
    private static final Map<String, String> FILTER_ALIASES = Map.of(
            "empresa", "empresa.nombre",
            "sucursal", "sucursal.nombre");

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;
    private final HashService passwordHasher;
    private final EntityMapper<Usuario, UsuarioSummaryResponse> usuarioMapper;
    private final CurrentUserScope currentUserScope;

    @Transactional(readOnly = true)
    public Page<UsuarioSummaryResponse> listarTodos(Pageable pageable, Map<String, String> filters)
    {
        Map<String, String> escalares = new HashMap<>(filters);
        String rol = escalares.remove("roles");

        var userSpec = new FilterSpecificationBuilder<Usuario>()
                .withAliases(FILTER_ALIASES)
                .withConditions(escalares)
                .build();

        Specification<Usuario> rolesSpec = (root, query, cb) -> cb.conjunction();
        if (rol != null && !rol.isBlank()) {
            rolesSpec = (root, query, cb) -> {
                query.distinct(true);
                return root.join("roles").as(String.class).in(rol);
            };
        }

        return usuarioRepository.findAll(scopeSpec().and(userSpec).and(rolesSpec), pageable)
                .map(usuarioMapper::toSummaryResponse);
    }

    @Transactional(readOnly = true)
    public List<UsuarioSummaryResponse> listarPorEmpresaSummary(Long empresaId) 
    {
        return usuarioRepository.findAll(scopeSpec().and(byEmpresaSpec(empresaId)))
                .stream()
                .map(usuarioMapper::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioSummaryResponse> listarPorSucursalSummary(Long sucursalId) 
    {
        return usuarioRepository.findAll(scopeSpec().and(bySucursalSpec(sucursalId)))
                .stream()
                .map(usuarioMapper::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) 
    {
        return usuarioRepository.findOne(scopeSpec().and(byIdSpec(id)))
                .orElseThrow(() -> new UserNotFoundException("ID " + id));
    }

    @Transactional
    public Usuario crear(UsuarioRequest request) 
    {
        if (usuarioRepository.existsByEmail(request.getEmail()))
            throw new EmailAlreadyExistsException(request.getEmail());

        if (request.getRoles() == null || request.getRoles().isEmpty())
            throw new AccesoDenegadoException("Debes especificar al menos un rol");

        TokenUser currentUser = getCurrentUser();

        if (currentUser.roles().contains(Rol.SUPER_ADMIN)) 
        {
            // SUPER_ADMIN puede crear cualquier rol
        } 
        else if (currentUser.roles().contains(Rol.ADMIN_EMPRESA)) 
        {
            // ADMIN_EMPRESA solo puede crear ADMIN_SUCURSAL, USUARIO
            validarRolesAsignables(request.getRoles());
            // Debe asignar su misma empresa
            if (request.getEmpresaId() == null || !request.getEmpresaId().equals(currentUser.empresaId())) 
            {
                throw new AccesoDenegadoException("Solo puedes crear usuarios en tu propia empresa");
            }
        } 
        else 
        {
            throw new AccesoDenegadoException("No tienes permiso para crear usuarios");
        }

        Empresa empresa = null;
        if (request.getEmpresaId() != null) {
            empresa = empresaRepository.findById(request.getEmpresaId())
                    .orElseThrow(() -> new EmpresaNotFoundException("ID " + request.getEmpresaId()));

            if (empresa.getDeletedAt() != null)
            {
                throw new EmpresaNotFoundException("ID " + request.getEmpresaId());
            }
        }

        Sucursal sucursal = null;
        if (request.getSucursalId() != null) {
            sucursal = findSucursalEnScope(request.getSucursalId());
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
            throw new NoAutenticadoException("Usuario no autenticado");
        }
        return (TokenUser) auth.getPrincipal();
    }

    @Transactional
    public Usuario actualizar(Long id, UsuarioRequest request) 
    {
        Usuario usuario = buscarActivaPorId(id);

        TokenUser currentUser = getCurrentUser();

        if (!currentUser.roles().contains(Rol.SUPER_ADMIN)) 
        {
            validarNoModificaAdmin(usuario);
            validarRolesAsignables(request.getRoles());
        }

        usuario.setNombre(request.getNombre());
        usuario.setTelefono(request.getTelefono());

        if (request.getEmpresaId() != null) 
        {
            if (!currentUser.roles().contains(Rol.SUPER_ADMIN)
                    && !request.getEmpresaId().equals(currentUser.empresaId())) 
            {
                throw new AccesoDenegadoException("Solo puedes asignar tu propia empresa");
            }

            Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                    .orElseThrow(() -> new EmpresaNotFoundException("ID " + request.getEmpresaId()));

            if (empresa.getDeletedAt() != null)
            {
                throw new EmpresaNotFoundException("ID " + request.getEmpresaId());
            }

            usuario.setEmpresa(empresa);
        } 
        else 
        {
            usuario.setEmpresa(null);
        }

        if (request.getSucursalId() != null) 
        {
            Sucursal sucursal = findSucursalEnScope(request.getSucursalId());
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
        Usuario usuario = buscarActivaPorId(id);
        usuario.setPasswordHash(passwordHasher.hash(nuevaPassword));
    }

    @Transactional
    public void activar(Long id) 
    {
        Usuario usuario = buscarActivaPorId(id);
        usuario.setActivo(true);
    }

    @Transactional
    public void desactivar(Long id) 
    {
        Usuario usuario = buscarActivaPorId(id);
        usuario.setActivo(false);
    }

    @Transactional
    public void eliminar(Long id) 
    {
        Usuario usuario = buscarActivaPorId(id);
        usuario.setDeletedAt(Instant.now());
    }

    @Transactional
    public Usuario restaurar(Long id) 
    {
        Usuario usuario = usuarioRepository.findOne(scopeSpec().and(byIdSpec(id)))
                .orElseThrow(() -> new UserNotFoundException("ID " + id));

        usuario.setDeletedAt(null);

        return usuario;
    }

    private Usuario buscarActivaPorId(Long id)
    {
        Usuario usuario = buscarPorId(id);

        if (usuario.getDeletedAt() != null)
        {
            throw new UserNotFoundException("ID " + id);
        }

        return usuario;
    }

    private Sucursal findSucursalEnScope(Long sucursalId)
    {
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new SucursalNotFoundException("ID " + sucursalId));

        currentUserScope.assertAccess(sucursal.getId(), sucursal.getEmpresa().getId());

        return sucursal;
    }

    private void validarRolesAsignables(Set<Rol> roles)
    {
        if (roles == null)
        {
            return;
        }

        for (Rol rol : roles)
        {
            if (rol == Rol.SUPER_ADMIN || rol == Rol.ADMIN_EMPRESA)
            {
                throw new AccesoDenegadoException("No puedes asignar el rol " + rol);
            }
        }
    }

    private void validarNoModificaAdmin(Usuario usuario)
    {
        if (usuario.getRoles().contains(Rol.SUPER_ADMIN) || usuario.getRoles().contains(Rol.ADMIN_EMPRESA))
        {
            throw new AccesoDenegadoException("No puedes modificar un usuario SUPER_ADMIN o ADMIN_EMPRESA");
        }
    }

    private Specification<Usuario> scopeSpec()
    {
        return currentUserScope.scopeSpec("empresa.id", "sucursal.id");
    }

    private Specification<Usuario> byIdSpec(Long id)
    {
        return new FilterSpecificationBuilder<Usuario>()
                .withCondition(new FilterCondition("id", FilterOperator.EQ, id))
                .build();
    }

    private Specification<Usuario> byEmpresaSpec(Long empresaId)
    {
        return new FilterSpecificationBuilder<Usuario>()
                .withCondition(new FilterCondition("empresa.id", FilterOperator.EQ, empresaId))
                .build();
    }

    private Specification<Usuario> bySucursalSpec(Long sucursalId)
    {
        return new FilterSpecificationBuilder<Usuario>()
                .withCondition(new FilterCondition("sucursal.id", FilterOperator.EQ, sucursalId))
                .build();
    }
}
