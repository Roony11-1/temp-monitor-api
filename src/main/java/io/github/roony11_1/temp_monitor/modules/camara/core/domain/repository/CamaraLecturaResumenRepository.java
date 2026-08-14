package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.CamaraLecturaResumen;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.GranularidadLectura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CamaraLecturaResumenRepository extends JpaRepository<CamaraLecturaResumen, Long>
{
    List<CamaraLecturaResumen> findByCamaraIdAndGranularidadOrderByBucketStartDesc(
            Long camaraId, GranularidadLectura granularidad);
}