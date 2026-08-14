package io.github.roony11_1.temp_monitor.modules.camara.core.application;

import io.github.roony11_1.temp_monitor.config.CamaraMuestreoConfig;
import io.github.roony11_1.temp_monitor.kernel.security.scope.CurrentUserScope;
import io.github.roony11_1.specification.core.FilterCondition;
import io.github.roony11_1.specification.core.FilterOperator;
import io.github.roony11_1.specification.spring.FilterSpecificationBuilder;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.exceptions.CamaraNotFoundException;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.CamaraLectura;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.CamaraLecturaResumen;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.EstadoSensor;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.GranularidadLectura;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraLecturaRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraLecturaResumenRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraRepository;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.LecturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CamaraLecturaService 
{
    public static final Duration VENTANA_MUESTRA = Duration.ofMinutes(15);

    private final CamaraRepository camaraRepository;
    private final CamaraLecturaRepository camaraLecturaRepository;
    private final CamaraLecturaResumenRepository camaraLecturaResumenRepository;
    private final LecturaRepository lecturaRepository;
    private final CurrentUserScope currentUserScope;
    private final CamaraMuestreoConfig camaraMuestreoConfig;

    private Duration cadencia()
    {
        return Duration.ofSeconds(camaraMuestreoConfig.getCadenciaSegundos());
    }

    @Transactional
    public void muestrear()
    {
        Instant ahora = Instant.now();
        var noEliminadas = new FilterSpecificationBuilder<Camara>()
                .withCondition(new FilterCondition("deletedAt", FilterOperator.IS_NULL, null))
                .build();
        camaraRepository.findAll(noEliminadas).forEach(camara -> muestrearCamara(camara, ahora));
    }

    @Transactional
    public void muestrearCamara(Camara camara, Instant momento)
    {
        Instant bucketStart = bucketStart(momento);
        Instant desde = bucketStart.minus(VENTANA_MUESTRA);

        Double promedio = lecturaRepository.calcularPromedioPorCamara(camara.getId(), desde, EstadoSensor.ACTIVO);
        long sensores = lecturaRepository.contarSensoresConDatosPorCamara(camara.getId(), desde, EstadoSensor.ACTIVO);

        if (sensores == 0)
        {
            return;
        }

        CamaraLectura muestra = camaraLecturaRepository
            .findByCamaraIdAndBucketStart(camara.getId(), bucketStart)
            .orElseGet(() -> CamaraLectura.builder()
                .camara(camara)
                .bucketStart(bucketStart)
                .build());

        muestra.setMuestreadoEn(momento);
        muestra.setPromedio(promedio != null ? promedio : 0.0);
        muestra.setConteoSensores((int) sensores);

        camaraLecturaRepository.save(muestra);
    }

    @Transactional(readOnly = true)
    public List<CamaraLectura> listarPorCamara(Long camaraId, Instant since)
    {
        assertCamaraEnScope(camaraId);

        return camaraLecturaRepository.findByCamaraIdAndBucketStartAfterOrderByBucketStartAsc(camaraId, since);
    }

    @Transactional(readOnly = true)
    public List<CamaraLecturaResumen> listarResumenPorCamara(Long camaraId, GranularidadLectura granularidad)
    {
        assertCamaraEnScope(camaraId);

        return camaraLecturaResumenRepository.findByCamaraIdAndGranularidadOrderByBucketStartDesc(camaraId, granularidad);
    }

    private void assertCamaraEnScope(Long camaraId)
    {
        var byId = new FilterSpecificationBuilder<Camara>()
                .withCondition(new FilterCondition("id", FilterOperator.EQ, camaraId))
                .build();
        camaraRepository.findOne(currentUserScope.<Camara>scopeSpec("sucursal.empresa.id", "sucursal.id").and(byId))
            .orElseThrow(() -> new CamaraNotFoundException("ID " + camaraId));
    }

    private Instant bucketStart(Instant momento)
    {
        long epoch = momento.getEpochSecond();
        long bucket = epoch - Math.floorMod(epoch, camaraMuestreoConfig.getCadenciaSegundos());
        return Instant.ofEpochSecond(bucket);
    }
}