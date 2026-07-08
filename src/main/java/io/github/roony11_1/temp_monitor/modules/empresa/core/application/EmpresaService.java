package io.github.roony11_1.temp_monitor.modules.empresa.core.application;

import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.EmpresaNotFoundException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.NombreEmpresaAlreadyExistsException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaService 
{
    private final EmpresaRepository empresaRepository;

    public List<Empresa> listarTodas() 
    {
        return empresaRepository.findAll();
    }

    public Empresa buscarPorId(Long id) 
    {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaNotFoundException("ID " + id));
    }

    @Transactional
    public Empresa crear(String nombre, String direccion, String telefono, String email) 
    {
        if (empresaRepository.existsByNombre(nombre))
            throw new NombreEmpresaAlreadyExistsException(nombre);

        Empresa empresa = Empresa.builder()
                .nombre(nombre)
                .direccion(direccion)
                .telefono(telefono)
                .email(email)
                .activo(true)
                .build();

        return empresaRepository.save(empresa);
    }

    @Transactional
    public Empresa actualizar(Long id, String nombre, String direccion, String telefono, String email) 
    {
        Empresa empresa = buscarPorId(id);

        empresa.setNombre(nombre);
        empresa.setDireccion(direccion);
        empresa.setTelefono(telefono);
        empresa.setEmail(email);
        empresa.setUpdatedAt(Instant.now());
        return empresaRepository.save(empresa);
    }

    @Transactional
    public void activar(Long id) 
    {
        Empresa empresa = buscarPorId(id);
        empresa.setActivo(true);
        empresa.setUpdatedAt(Instant.now());
        empresaRepository.save(empresa);
    }

    @Transactional
    public void desactivar(Long id) 
    {
        Empresa empresa = buscarPorId(id);
        empresa.setActivo(false);
        empresa.setUpdatedAt(Instant.now());
        empresaRepository.save(empresa);
    }

    @Transactional
    public void eliminar(Long id) 
    {
        var empresa = buscarPorId(id);
        empresaRepository.delete(empresa);
    }
}
