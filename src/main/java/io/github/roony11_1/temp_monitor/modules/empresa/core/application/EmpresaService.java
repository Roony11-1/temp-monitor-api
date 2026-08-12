package io.github.roony11_1.temp_monitor.modules.empresa.core.application;

import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.kernel.security.scope.CurrentUserScope;
import io.github.roony11_1.specification.core.FilterCondition;
import io.github.roony11_1.specification.core.FilterOperator;
import io.github.roony11_1.specification.spring.FilterSpecificationBuilder;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.EmpresaRequest;
import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.EmpresaSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.EmpresaNotFoundException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.NombreEmpresaAlreadyExistsException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmpresaService 
{
    private final EmpresaRepository empresaRepository;
    private final EntityMapper<Empresa, EmpresaSummaryResponse> empresaMapper;
    private final CurrentUserScope currentUserScope;

    @Transactional(readOnly = true)
    public Page<EmpresaSummaryResponse> listarTodas(Pageable pageable, Map<String, String> filters)
    {
        var userSpec = new FilterSpecificationBuilder<Empresa>()
                .withConditions(filters)
                .build();
        return empresaRepository.findAll(empresaScope().and(userSpec), pageable)
                .map(empresaMapper::toSummaryResponse);
    }

    public Empresa buscarPorId(Long id) 
    {
        return empresaRepository.findOne(empresaScope().and(byIdSpec(id)))
                .orElseThrow(() -> new EmpresaNotFoundException("ID " + id));
    }

    @Transactional
    public Empresa crear(EmpresaRequest request) 
    {
        if (empresaRepository.existsByNombre(request.getNombre()))
            throw new NombreEmpresaAlreadyExistsException(request.getNombre());

        Empresa empresa = request.fromRequest();

        return empresaRepository.save(empresa);
    }

    @Transactional
    public Empresa actualizar(Long id, EmpresaRequest request) 
    {
        Empresa empresa = buscarPorId(id);

        empresa.setNombre(request.getNombre());
        empresa.setDireccion(request.getDireccion());
        empresa.setTelefono(request.getTelefono());
        empresa.setEmail(request.getEmail());

        return empresa;
    }

    @Transactional
    public void activar(Long id) 
    {
        Empresa empresa = buscarPorId(id);

        empresa.setActivo(true);
    }

    @Transactional
    public void desactivar(Long id) 
    {
        Empresa empresa = buscarPorId(id);

        empresa.setActivo(false);
    }

    @Transactional
    public void eliminar(Long id) 
    {
        var empresa = buscarPorId(id);

        empresaRepository.delete(empresa);
    }

    private Specification<Empresa> empresaScope()
    {
        return currentUserScope.scopeEmpresaOnlySpec("id");
    }

    private Specification<Empresa> byIdSpec(Long id)
    {
        return new FilterSpecificationBuilder<Empresa>()
                .withCondition(new FilterCondition("id", FilterOperator.EQ, id))
                .build();
    }
}
