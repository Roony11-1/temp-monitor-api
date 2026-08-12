package io.github.roony11_1.temp_monitor.modules.empresa.core.application;

import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.kernel.security.scope.CurrentUserScope;
import io.github.roony11_1.specification.spring.FilterSpecificationBuilder;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.SucursalRequest;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.SucursalSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.EmpresaNotFoundException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.NombreSucursalAlreadyExistsException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.SucursalNotFoundException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.EmpresaRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SucursalService 
{
    private static final Map<String, String> FILTER_ALIASES = Map.of(
            "empresa", "empresa.nombre");

    private final SucursalRepository sucursalRepository;
    private final EmpresaRepository empresaRepository;
    private final EntityMapper<Sucursal, SucursalSummaryResponse> sucursalMapper;
    private final CurrentUserScope currentUserScope;

    @Transactional(readOnly = true)
    public Page<SucursalSummaryResponse> listarTodas(Pageable pageable, Map<String, String> filters)
    {
        var userSpec = new FilterSpecificationBuilder<Sucursal>()
                .withAliases(FILTER_ALIASES)
                .withConditions(filters)
                .build();
        return sucursalRepository.findAll(scopeSpec().and(userSpec), pageable)
                .map(sucursalMapper::toSummaryResponse);
    }

    @Transactional(readOnly = true)
    public List<SucursalSummaryResponse> listarPorEmpresaSummary(Long empresaId) 
    {
        return sucursalRepository.findByEmpresaId(empresaId).stream()
                .filter(s -> currentUserScope.canAccess(s.getId(), s.getEmpresa().getId()))
                .map(sucursalMapper::toSummaryResponse)
                .toList();
    }

    public Sucursal buscarPorId(Long id) 
    {
        return sucursalRepository.findOne(scopeSpec().and(byIdSpec(id)))
                .orElseThrow(() -> new SucursalNotFoundException("ID " + id));
    }

    @Transactional
    public Sucursal crear(SucursalRequest requst) 
    {
        Empresa empresa = empresaRepository.findById(requst.getEmpresaId())
                .orElseThrow(() -> new EmpresaNotFoundException("ID " + requst.getEmpresaId()));

        if (sucursalRepository.existsByNombre(requst.getNombre())) 
        {
            throw new NombreSucursalAlreadyExistsException(requst.getNombre());
        }

        Sucursal sucursal = Sucursal.builder()
                .nombre(requst.getNombre())
                .direccion(requst.getDireccion())
                .telefono(requst.getTelefono())
                .empresa(empresa)
                .activo(true)
                .build();

        return sucursalRepository.save(sucursal);
    }

    @Transactional
    public Sucursal actualizar(Long id, SucursalRequest request) 
    {
        Sucursal sucursal = buscarPorId(id);

        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setTelefono(request.getTelefono());
        
        if (request.getEmpresaId() != null) 
        {
            Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                    .orElseThrow(() -> new EmpresaNotFoundException("ID " + request.getEmpresaId()));
            sucursal.setEmpresa(empresa);
        }

        return sucursal;
    }

    @Transactional
    public void activar(Long id) 
    {
        Sucursal sucursal = buscarPorId(id);
        sucursal.setActivo(true);
    }

    @Transactional
    public void desactivar(Long id) 
    {
        Sucursal sucursal = buscarPorId(id);
        sucursal.setActivo(false);
    }

    @Transactional
    public void eliminar(Long id) 
    {
        var sucursal = buscarPorId(id);
        sucursalRepository.delete(sucursal);
    }

    private Specification<Sucursal> scopeSpec()
    {
        return currentUserScope.scopeSpec("empresa.id", "id");
    }

    private Specification<Sucursal> byIdSpec(Long id)
    {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }
}
