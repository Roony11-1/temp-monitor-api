package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraRequest;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.CamaraNotFoundException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.SucursalNotFoundException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Page<Camara> listarTodas(Pageable pageable) 
    {
        return camaraRepository.findAll(pageable);
    }

    public List<Camara> listarPorSucursal(Long sucursalId) 
    {
        return camaraRepository.findBySucursalId(sucursalId);
    }

    public Page<Camara> listarPorSucursal(Long sucursalId, Pageable pageable) 
    {
        return camaraRepository.findBySucursalId(sucursalId, pageable);
    }

    public Camara buscarPorId(Long id) 
    {
        return camaraRepository.findById(id)
                .orElseThrow(() -> new CamaraNotFoundException("ID " + id));
    }

    @Transactional
    public Camara crear(CamaraRequest request) 
    {
        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new SucursalNotFoundException("ID " + request.getSucursalId()));

        Camara camara = Camara.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .sucursal(sucursal)
                .activo(true)
                .build();

        return camaraRepository.save(camara);
    }

    @Transactional
    public Camara actualizar(Long id, CamaraRequest request) 
    {
        Camara camara = buscarPorId(id);

        camara.setNombre(request.getNombre());
        camara.setDescripcion(request.getDescripcion());
        
        if (request.getSucursalId() != null) 
        {
            Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                    .orElseThrow(() -> new SucursalNotFoundException("ID " + request.getSucursalId()));

            camara.setSucursal(sucursal);
        }

        return camaraRepository.save(camara);
    }

    @Transactional
    public void activar(Long id) 
    {
        Camara camara = buscarPorId(id);

        camara.setActivo(true);

        camaraRepository.save(camara);
    }

    @Transactional
    public void desactivar(Long id) 
    {
        Camara camara = buscarPorId(id);

        camara.setActivo(false);

        camaraRepository.save(camara);
    }

    @Transactional
    public void eliminar(Long id) 
    {
        var camara = buscarPorId(id);

        camaraRepository.delete(camara);
    }
}
