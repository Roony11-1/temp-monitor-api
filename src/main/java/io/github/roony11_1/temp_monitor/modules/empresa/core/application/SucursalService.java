package io.github.roony11_1.temp_monitor.modules.empresa.core.application;

import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.kernel.specification.FilterSpecificationBuilder;
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

    public List<Sucursal> listarTodas() 
    {
        return sucursalRepository.findAll();
    }

    public Page<Sucursal> listarTodas(Pageable pageable) 
    {
        return sucursalRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<SucursalSummaryResponse> listarTodas(Pageable pageable, Map<String, String> filters)
    {
        var spec = new FilterSpecificationBuilder<Sucursal>()
                .withAliases(FILTER_ALIASES)
                .withConditions(filters)
                .build();
        return sucursalRepository.findAll(spec, pageable)
                .map(sucursalMapper::toSummaryResponse);
    }

    public List<Sucursal> listarPorEmpresa(Long empresaId) 
    {
        return sucursalRepository.findByEmpresaId(empresaId);
    }

    @Transactional(readOnly = true)
    public List<SucursalSummaryResponse> listarPorEmpresaSummary(Long empresaId) 
    {
        return sucursalRepository.findByEmpresaId(empresaId).stream()
                .map(sucursalMapper::toSummaryResponse)
                .toList();
    }

    public Page<Sucursal> listarPorEmpresa(Long empresaId, Pageable pageable) 
    {
        return sucursalRepository.findByEmpresaId(empresaId, pageable);
    }

    public Sucursal buscarPorId(Long id) 
    {
        return sucursalRepository.findById(id)
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
}
