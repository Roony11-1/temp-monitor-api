package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraTemperaturaResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.UltimaLecturaSensorResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.camara.response.CamaraSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.application.mapper.CamaraMapper;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.CamaraNotFoundException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.RangoTemperaturaInvalidoException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.LecturaRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.SucursalNotFoundException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.SucursalRepository;
import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.kernel.specification.FilterSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CamaraService 
{
    private static final Duration VENTANA_TEMPERATURA = Duration.ofMinutes(15);

    private final CamaraRepository camaraRepository;
    private final SucursalRepository sucursalRepository;
    private final LecturaRepository lecturaRepository;

    private final EntityMapper<Camara, CamaraSummaryResponse> camaraMapper;

    public List<Camara> listarTodas() 
    {
        return camaraRepository.findAll();
    }

    public Page<Camara> listarTodas(Pageable pageable) 
    {
        return camaraRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<CamaraSummaryResponse> listarTodas(Pageable pageable, Map<String, String> filters)
    {
        var spec = new FilterSpecificationBuilder<Camara>()
                .withConditions(filters)
                .build();
        return camaraRepository.findAll(spec, pageable)
                .map(camaraMapper::toSummaryResponse);
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
        validarRango(request.getTemperaturaMin(), request.getTemperaturaMax());

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new SucursalNotFoundException("ID " + request.getSucursalId()));

        Camara camara = Camara.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .sucursal(sucursal)
                .temperaturaMin(request.getTemperaturaMin())
                .temperaturaMax(request.getTemperaturaMax())
                .activo(true)
                .build();

        return camaraRepository.save(camara);
    }

    @Transactional
    public Camara actualizar(Long id, CamaraRequest request) 
    {
        validarRango(request.getTemperaturaMin(), request.getTemperaturaMax());

        Camara camara = buscarPorId(id);

        camara.setNombre(request.getNombre());
        camara.setDescripcion(request.getDescripcion());
        camara.setTemperaturaMin(request.getTemperaturaMin());
        camara.setTemperaturaMax(request.getTemperaturaMax());
        
        if (request.getSucursalId() != null) 
        {
            Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                    .orElseThrow(() -> new SucursalNotFoundException("ID " + request.getSucursalId()));

            camara.setSucursal(sucursal);
        }

        return camara;
    }

    @Transactional
    public void activar(Long id) 
    {
        Camara camara = buscarPorId(id);

        camara.setActivo(true);
    }

    @Transactional
    public void desactivar(Long id) 
    {
        Camara camara = buscarPorId(id);

        camara.setActivo(false);
    }

    private void validarRango(Double min, Double max) 
    {
        if (min != null && max != null && min >= max) 
        {
            throw new RangoTemperaturaInvalidoException();
        }
    }

    @Transactional
    public void eliminar(Long id) 
    {
        var camara = buscarPorId(id);

        camaraRepository.delete(camara);
    }

    @Transactional(readOnly = true)
    public CamaraTemperaturaResponse obtenerTemperatura(Long id)
    {
        buscarPorId(id);

        Instant since = Instant.now().minus(VENTANA_TEMPERATURA);

        Double promedio = lecturaRepository.calcularPromedioPorCamara(id, since, EstadoSensor.ACTIVO);
        long sensoresConDatos = lecturaRepository.contarSensoresConDatosPorCamara(id, since, EstadoSensor.ACTIVO);
        Instant ultimaLectura = lecturaRepository.findUltimaLecturaPorCamara(id).orElse(null);

        return CamaraTemperaturaResponse.builder()
                .promedio(promedio != null ? Math.round(promedio * 10.0) / 10.0 : null)
                .sensoresConDatos(sensoresConDatos)
                .ultimaLectura(ultimaLectura)
                .build();
    }

    @Transactional(readOnly = true)
    public List<UltimaLecturaSensorResponse> obtenerUltimasMedidas(Long id)
    {
        buscarPorId(id);

        return lecturaRepository.findUltimaPorSensorDeCamara(id).stream()
                .map(l -> UltimaLecturaSensorResponse.builder()
                        .sensorUuid(l.getSensorUuid())
                        .temperatura(l.getTemperatura())
                        .timestamp(l.getTimestamp())
                        .build())
                .toList();
    }
}
