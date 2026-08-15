package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraRequest;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.CamaraTemperaturaResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.UltimaLecturaSensorResponse;
import io.github.roony11_1.temp_monitor.modules.camara.api.dto.camara.response.CamaraSummaryResponse;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.CamaraNotFoundException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.RangoTemperaturaInvalidoException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.CamaraLectura;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraLecturaRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.LecturaRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.exceptions.SucursalNotFoundException;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.SucursalRepository;
import io.github.roony11_1.temp_monitor.kernel.cascade.CascadeStateService;
import io.github.roony11_1.temp_monitor.kernel.mapper.DetailEntityMapper;
import io.github.roony11_1.temp_monitor.kernel.mapper.EntityMapper;
import io.github.roony11_1.temp_monitor.kernel.security.scope.CurrentUserScope;
import io.github.roony11_1.specification.core.FilterCondition;
import io.github.roony11_1.specification.core.FilterOperator;
import io.github.roony11_1.specification.spring.FilterSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class CamaraService 
{
    private static final Duration VENTANA_TEMPERATURA = Duration.ofMinutes(15);

    private static final Map<String, String> FILTER_ALIASES = Map.of(
            "sucursal", "sucursal.nombre",
            "estado", "activo");

    private final CamaraRepository camaraRepository;
    private final SucursalRepository sucursalRepository;
    private final LecturaRepository lecturaRepository;
    private final CamaraLecturaRepository camaraLecturaRepository;

    private final EntityMapper<Camara, CamaraSummaryResponse> camaraMapper;
    private final DetailEntityMapper<Camara, CamaraResponse> camaraDetailMapper;
    private final CurrentUserScope currentUserScope;
    private final CascadeStateService cascadeStateService;

    @Transactional(readOnly = true)
    public Page<CamaraSummaryResponse> listarTodas(Pageable pageable, Map<String, String> filters)
    {
        var userSpec = new FilterSpecificationBuilder<Camara>()
                .withAliases(FILTER_ALIASES)
                .withConditions(filters)
                .build();

        var page = camaraRepository.findAll(scopeSpec().and(userSpec), pageable);

        Map<Long, Double> ultimaPorCamara = camaraLecturaRepository
                .findUltimaPorCamaraIds(page.getContent().stream().map(Camara::getId).toList())
                .stream()
                .collect(Collectors.toMap(cl -> cl.getCamara().getId(), CamaraLectura::getPromedio, (a, b) -> a));

        return page.map(camara -> {
            CamaraSummaryResponse summary = camaraMapper.toSummaryResponse(camara);
            summary.setTemperaturaActual(ultimaPorCamara.get(camara.getId()));
            return summary;
        });
    }

    @Transactional(readOnly = true)
    public List<CamaraResponse> listarPorSucursal(Long sucursalId)
    {
        return camaraRepository.findAll(scopeSpec().and(bySucursalSpec(sucursalId)), Sort.unsorted()).stream()
                .map(camaraDetailMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CamaraResponse buscarPorId(Long id)
    {
        return camaraDetailMapper.toResponse(buscarEntidadPorId(id));
    }

    private Camara buscarEntidadPorId(Long id)
    {
        return camaraRepository.findOne(scopeSpec().and(byIdSpec(id)))
                .orElseThrow(() -> new CamaraNotFoundException("ID " + id));
    }

    private Specification<Camara> scopeSpec()
    {
        return currentUserScope.scopeSpec("sucursal.empresa.id", "sucursal.id");
    }

    private Specification<Camara> byIdSpec(Long id)
    {
        return new FilterSpecificationBuilder<Camara>()
                .withCondition(new FilterCondition("id", FilterOperator.EQ, id))
                .build();
    }

    private Specification<Camara> bySucursalSpec(Long sucursalId)
    {
        return new FilterSpecificationBuilder<Camara>()
                .withCondition(new FilterCondition(
                        "sucursal.id", FilterOperator.EQ, sucursalId))
                .build();
    }

    @Transactional
    public CamaraResponse crear(CamaraRequest request) 
    {
        validarRango(request.getTemperaturaMin(), request.getTemperaturaMax());

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new SucursalNotFoundException("ID " + request.getSucursalId()));

        if (sucursal.getDeletedAt() != null)
        {
            throw new SucursalNotFoundException("ID " + request.getSucursalId());
        }

        currentUserScope.assertAccess(sucursal.getId(), sucursal.getEmpresa().getId());

        Camara camara = Camara.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .sucursal(sucursal)
                .temperaturaMin(request.getTemperaturaMin())
                .temperaturaMax(request.getTemperaturaMax())
                .activo(true)
                .build();

        return camaraDetailMapper.toResponse(camaraRepository.save(camara));
    }

    @Transactional
    public CamaraResponse actualizar(Long id, CamaraRequest request) 
    {
        validarRango(request.getTemperaturaMin(), request.getTemperaturaMax());

        Camara camara = buscarActivaPorId(id);

        camara.setNombre(request.getNombre());
        camara.setDescripcion(request.getDescripcion());
        camara.setTemperaturaMin(request.getTemperaturaMin());
        camara.setTemperaturaMax(request.getTemperaturaMax());
        
        if (request.getSucursalId() != null) 
        {
            Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                    .orElseThrow(() -> new SucursalNotFoundException("ID " + request.getSucursalId()));

            if (sucursal.getDeletedAt() != null)
            {
                throw new SucursalNotFoundException("ID " + request.getSucursalId());
            }

            currentUserScope.assertAccess(sucursal.getId(), sucursal.getEmpresa().getId());

            camara.setSucursal(sucursal);
        }

        return camaraDetailMapper.toResponse(camara);
    }

    @Transactional
    public void activar(Long id) 
    {
        Camara camara = buscarActivaPorId(id);

        cascadeStateService.activarCamara(camara);
    }

    @Transactional
    public void desactivar(Long id) 
    {
        Camara camara = buscarActivaPorId(id);

        cascadeStateService.desactivarCamara(camara);
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
        Camara camara = buscarActivaPorId(id);

        cascadeStateService.eliminarCamara(camara);
    }

    @Transactional
    public CamaraResponse restaurar(Long id) 
    {
        Camara camara = buscarEntidadPorId(id);

        cascadeStateService.restaurarCamara(camara);

        return camaraDetailMapper.toResponse(camara);
    }

    private Camara buscarActivaPorId(Long id)
    {
        Camara camara = buscarEntidadPorId(id);

        if (camara.getDeletedAt() != null)
        {
            throw new CamaraNotFoundException("ID " + id);
        }

        return camara;
    }

    @Transactional(readOnly = true)
    public CamaraTemperaturaResponse obtenerTemperatura(Long id)
    {
        buscarEntidadPorId(id);

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
        buscarEntidadPorId(id);

        return lecturaRepository.findUltimaPorSensorDeCamara(id).stream()
                .map(l -> UltimaLecturaSensorResponse.builder()
                        .sensorUuid(l.getSensorUuid())
                        .temperatura(l.getTemperatura())
                        .timestamp(l.getTimestamp())
                        .build())
                .toList();
    }
}
