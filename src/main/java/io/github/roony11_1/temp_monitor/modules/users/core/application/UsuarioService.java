package io.github.roony11_1.temp_monitor.modules.users.core.application;

import io.github.roony11_1.temp_monitor.kernel.mapper.DetailEntityMapper;
import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.kernel.security.exception.AccesoDenegadoException;
import io.github.roony11_1.temp_monitor.kernel.security.exception.NoAutenticadoException;
import io.github.roony11_1.temp_monitor.kernel.security.scope.CurrentUserScope;
import io.github.roony11_1.specification.spring.FilterSpecificationBuilder;
import io.github.roony11_1.temp_monitor.kernel.security.crypto.HashService;
import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.kernel.security.policy.RoleAssignmentPolicy;
import io.github.roony11_1.temp_monitor.kernel.spec.FilterParserAdapter;
import io.github.roony11_1.temp_monitor.kernel.spec.SpecificationFactory;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.EmpresaRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.SucursalRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.EmpresaNotFoundException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.SucursalNotFoundException;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioRequest;
import io.github.roony11_1.temp_monitor.modules.users.api.dto.UsuarioResponse;
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

@Service
@RequiredArgsConstructor
public class UsuarioService 
{
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;
    private final HashService passwordHasher;
    private final EntityMapper<Usuario, UsuarioSummaryResponse> usuarioMapper;
    private final DetailEntityMapper<Usuario, UsuarioResponse> usuarioDetailMapper;
    private final CurrentUserScope currentUserScope;
    private final FilterParserAdapter filterParserAdapter;
    private final RoleAssignmentPolicy roleAssignmentPolicy;

    @Transactional(readOnly = true)
    public Page<UsuarioSummaryResponse> listarTodos(Pageable pageable, Map<String, String> filters)
    {
        Map<String, String> escalares = new HashMap<>(filters);
        String rol = escalares.remove("roles");

        var userSpec = new FilterSpecificationBuilder<Usuario>()
                .withConditions(filterParserAdapter.parse(escalares, "usuario"))
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
    public UsuarioResponse buscarPorId(Long id) 
    {
        return usuarioDetailMapper.toResponse(buscarEntidadPorId(id));
    }

    private Usuario buscarEntidadPorId(Long id) 
    {
        return usuarioRepository.findOne(scopeSpec().and(byIdSpec(id)))
                .orElseThrow(() -> new UserNotFoundException("ID " + id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) 
    {
        if (usuarioRepository.existsByEmail(request.getEmail()))
            throw new EmailAlreadyExistsException(request.getEmail());

        if (request.getRoles() == null || request.getRoles().isEmpty())
            throw new AccesoDenegadoException("Debes especificar al menos un rol");

        TokenUser currentUser = getCurrentUser();
        roleAssignmentPolicy.assertCanAssignOnCreate(currentUser, request.getRoles(), request.getEmpresaId());

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

        return usuarioDetailMapper.toResponse(usuarioRepository.save(usuario));
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
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) 
    {
        Usuario usuario = buscarActivaPorId(id);

        TokenUser currentUser = getCurrentUser();
        roleAssignmentPolicy.assertCanModify(currentUser, usuario, request.getRoles());

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

        return usuarioDetailMapper.toResponse(usuario);
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
    public UsuarioResponse restaurar(Long id) 
    {
        Usuario usuario = buscarEntidadPorId(id);

        usuario.setDeletedAt(null);

        return usuarioDetailMapper.toResponse(usuario);
    }

    private Usuario buscarActivaPorId(Long id)
    {
        Usuario usuario = buscarEntidadPorId(id);

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

    private Specification<Usuario> scopeSpec()
    {
        return currentUserScope.scopeSpec("empresa.id", "sucursal.id");
    }

    private Specification<Usuario> byIdSpec(Long id)
    {
        return SpecificationFactory.byId(id);
    }

    private Specification<Usuario> byEmpresaSpec(Long empresaId)
    {
        return SpecificationFactory.byField("empresa.id", empresaId);
    }

    private Specification<Usuario> bySucursalSpec(Long sucursalId)
    {
        return SpecificationFactory.byField("sucursal.id", sucursalId);
    }
}
