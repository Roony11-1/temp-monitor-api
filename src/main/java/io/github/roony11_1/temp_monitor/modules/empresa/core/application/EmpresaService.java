package io.github.roony11_1.temp_monitor.modules.empresa.core.application;

import io.github.roony11_1.temp_monitor.modules.empresa.api.dto.EmpresaRequest;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.EmpresaNotFoundException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.NombreEmpresaAlreadyExistsException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.EmpresaRepository;
import io.github.roony11_1.temp_monitor.kernel.specification.FilterSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmpresaService 
{
    private final EmpresaRepository empresaRepository;

    public List<Empresa> listarTodas() 
    {
        return empresaRepository.findAll();
    }

    public Page<Empresa> listarTodas(Pageable pageable) 
    {
        return empresaRepository.findAll(pageable);
    }

    public Page<Empresa> listarTodas(Pageable pageable, Map<String, String> filters)
    {
        if (filters == null || filters.isEmpty()) {
            return empresaRepository.findAll(pageable);
        }

        var spec = FilterSpecification.<Empresa>from(filters);
        return empresaRepository.findAll(spec, pageable);
    }

    public Empresa buscarPorId(Long id) 
    {
        return empresaRepository.findById(id)
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
}
