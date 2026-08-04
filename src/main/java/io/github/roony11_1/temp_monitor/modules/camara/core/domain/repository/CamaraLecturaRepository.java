package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.CamaraLectura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CamaraLecturaRepository extends JpaRepository<CamaraLectura, Long>
{
    Optional<CamaraLectura> findByCamaraIdAndBucketStart(Long camaraId, Instant bucketStart);

    List<CamaraLectura> findByCamaraIdAndBucketStartAfterOrderByBucketStartAsc(Long camaraId, Instant since);
}
