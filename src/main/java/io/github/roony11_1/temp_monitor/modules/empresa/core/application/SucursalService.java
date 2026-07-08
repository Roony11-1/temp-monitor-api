package io.github.roony11_1.temp_monitor.modules.empresa.core.application;

import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.EmpresaNotFoundException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.SucursalNotFoundException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.EmpresaRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SucursalService 
{
    private final SucursalRepository sucursalRepository;
    private final EmpresaRepository empresaRepository;

    public List<Sucursal> listarTodas() 
    {
        return sucursalRepository.findAll();
    }

    public List<Sucursal> listarPorEmpresa(Long empresaId) 
    {
        return sucursalRepository.findByEmpresaId(empresaId);
    }

    public Sucursal buscarPorId(Long id) 
    {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new SucursalNotFoundException("ID " + id));
    }

    @Transactional
    public Sucursal crear(String nombre, String direccion, String telefono, Long empresaId) 
    {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EmpresaNotFoundException("ID " + empresaId));

        Sucursal sucursal = Sucursal.builder()
                .nombre(nombre)
                .direccion(direccion)
                .telefono(telefono)
                .empresa(empresa)
                .activo(true)
                .build();

        return sucursalRepository.save(sucursal);
    }

    @Transactional
    public Sucursal actualizar(Long id, String nombre, String direccion, String telefono, Long empresaId) 
    {
        Sucursal sucursal = buscarPorId(id);

        sucursal.setNombre(nombre);
        sucursal.setDireccion(direccion);
        sucursal.setTelefono(telefono);
        if (empresaId != null) 
        {
            Empresa empresa = empresaRepository.findById(empresaId)
                    .orElseThrow(() -> new EmpresaNotFoundException("ID " + empresaId));
            sucursal.setEmpresa(empresa);
        }
        sucursal.setUpdatedAt(Instant.now());
        return sucursalRepository.save(sucursal);
    }

    @Transactional
    public void activar(Long id) 
    {
        Sucursal sucursal = buscarPorId(id);
        sucursal.setActivo(true);
        sucursal.setUpdatedAt(Instant.now());
        sucursalRepository.save(sucursal);
    }

    @Transactional
    public void desactivar(Long id) 
    {
        Sucursal sucursal = buscarPorId(id);
        sucursal.setActivo(false);
        sucursal.setUpdatedAt(Instant.now());
        sucursalRepository.save(sucursal);
    }

    @Transactional
    public void eliminar(Long id) 
    {
        var sucursal = buscarPorId(id);
        sucursalRepository.delete(sucursal);
    }
}
