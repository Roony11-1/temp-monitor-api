package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.CamaraNotFoundException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.SucursalNotFoundException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CamaraService 
{
    private final CamaraRepository camaraRepository;
    private final SucursalRepository sucursalRepository;

    public List<Camara> listarTodas() 
    {
        return camaraRepository.findAll();
    }

    public List<Camara> listarPorSucursal(Long sucursalId) 
    {
        return camaraRepository.findBySucursalId(sucursalId);
    }

    public Camara buscarPorId(Long id) 
    {
        return camaraRepository.findById(id)
                .orElseThrow(() -> new CamaraNotFoundException("ID " + id));
    }

    @Transactional
    public Camara crear(String nombre, String descripcion, Long sucursalId, Double temperaturaMinima, Double temperaturaMaxima) 
    {
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new SucursalNotFoundException("ID " + sucursalId));

        Camara camara = Camara.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .sucursal(sucursal)
                .temperaturaMinima(temperaturaMinima)
                .temperaturaMaxima(temperaturaMaxima)
                .activo(true)
                .build();

        return camaraRepository.save(camara);
    }

    @Transactional
    public Camara actualizar(Long id, String nombre, String descripcion, Long sucursalId, Double temperaturaMinima, Double temperaturaMaxima) 
    {
        Camara camara = buscarPorId(id);

        camara.setNombre(nombre);
        camara.setDescripcion(descripcion);
        if (sucursalId != null) 
        {
            Sucursal sucursal = sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new SucursalNotFoundException("ID " + sucursalId));
            camara.setSucursal(sucursal);
        }
        camara.setTemperaturaMinima(temperaturaMinima);
        camara.setTemperaturaMaxima(temperaturaMaxima);
        camara.setUpdatedAt(Instant.now());
        return camaraRepository.save(camara);
    }

    @Transactional
    public void activar(Long id) 
    {
        Camara camara = buscarPorId(id);
        camara.setActivo(true);
        camara.setUpdatedAt(Instant.now());
        camaraRepository.save(camara);
    }

    @Transactional
    public void desactivar(Long id) 
    {
        Camara camara = buscarPorId(id);
        camara.setActivo(false);
        camara.setUpdatedAt(Instant.now());
        camaraRepository.save(camara);
    }

    @Transactional
    public void eliminar(Long id) 
    {
        var camara = buscarPorId(id);
        camaraRepository.delete(camara);
    }
}
