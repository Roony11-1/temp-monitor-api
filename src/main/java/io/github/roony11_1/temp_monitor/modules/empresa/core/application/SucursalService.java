package io.github.roony11_1.temp_monitor.modules.empresa.core.application;

import io.github.roony11_1.temp_monitor.kernel.cascade.CascadeStateService;
import io.github.roony11_1.temp_monitor.kernel.mapper.DetailEntityMapper;
import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.kernel.security.scope.CurrentUserScope;
import io.github.roony11_1.specification.core.FilterCondition;
import io.github.roony11_1.specification.core.FilterOperator;
import io.github.roony11_1.specification.spring.FilterSpecificationBuilder;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.SucursalRequest;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.SucursalResponse;
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
    private final DetailEntityMapper<Sucursal, SucursalResponse> sucursalDetailMapper;
    private final CurrentUserScope currentUserScope;
    private final CascadeStateService cascadeStateService;

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
        return sucursalRepository.findAll(scopeSpec().and(byEmpresaSpec(empresaId)))
                .stream()
                .map(sucursalMapper::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SucursalResponse buscarPorId(Long id) 
    {
        return sucursalDetailMapper.toResponse(buscarEntidadPorId(id));
    }

    private Sucursal buscarEntidadPorId(Long id) 
    {
        return sucursalRepository.findOne(scopeSpec().and(byIdSpec(id)))
                .orElseThrow(() -> new SucursalNotFoundException("ID " + id));
    }

    @Transactional
    public SucursalResponse crear(SucursalRequest requst) 
    {
        Empresa empresa = empresaRepository.findById(requst.getEmpresaId())
                .orElseThrow(() -> new EmpresaNotFoundException("ID " + requst.getEmpresaId()));

        if (empresa.getDeletedAt() != null)
        {
            throw new EmpresaNotFoundException("ID " + requst.getEmpresaId());
        }

        currentUserScope.assertAccess(null, empresa.getId());

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

        return sucursalDetailMapper.toResponse(sucursalRepository.save(sucursal));
    }

    @Transactional
    public SucursalResponse actualizar(Long id, SucursalRequest request) 
    {
        Sucursal sucursal = buscarActivaPorId(id);

        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setTelefono(request.getTelefono());
        
        if (request.getEmpresaId() != null) 
        {
            Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                    .orElseThrow(() -> new EmpresaNotFoundException("ID " + request.getEmpresaId()));
            currentUserScope.assertAccess(null, empresa.getId());
            sucursal.setEmpresa(empresa);
        }

        return sucursalDetailMapper.toResponse(sucursal);
    }

    @Transactional
    public void activar(Long id) 
    {
        Sucursal sucursal = buscarActivaPorId(id);
        cascadeStateService.activarSucursal(sucursal);
    }

    @Transactional
    public void desactivar(Long id) 
    {
        Sucursal sucursal = buscarActivaPorId(id);
        cascadeStateService.desactivarSucursal(sucursal);
    }

    @Transactional
    public void eliminar(Long id) 
    {
        Sucursal sucursal = buscarActivaPorId(id);
        cascadeStateService.eliminarSucursal(sucursal);
    }

    @Transactional
    public SucursalResponse restaurar(Long id) 
    {
        Sucursal sucursal = buscarEntidadPorId(id);

        cascadeStateService.restaurarSucursal(sucursal);

        return sucursalDetailMapper.toResponse(sucursal);
    }

    private Sucursal buscarActivaPorId(Long id)
    {
        Sucursal sucursal = buscarEntidadPorId(id);

        if (sucursal.getDeletedAt() != null)
        {
            throw new SucursalNotFoundException("ID " + id);
        }

        return sucursal;
    }

    private Specification<Sucursal> scopeSpec()
    {
        return currentUserScope.scopeSpec("empresa.id", "id");
    }

    private Specification<Sucursal> byIdSpec(Long id)
    {
        return new FilterSpecificationBuilder<Sucursal>()
                .withCondition(new FilterCondition("id", FilterOperator.EQ, id))
                .build();
    }

    private Specification<Sucursal> byEmpresaSpec(Long empresaId)
    {
        return new FilterSpecificationBuilder<Sucursal>()
                .withCondition(new FilterCondition("empresa.id", FilterOperator.EQ, empresaId))
                .build();
    }
}
