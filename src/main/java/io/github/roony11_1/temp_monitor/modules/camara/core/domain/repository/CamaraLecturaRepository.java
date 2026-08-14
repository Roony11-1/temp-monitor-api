package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.CamaraLectura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CamaraLecturaRepository extends JpaRepository<CamaraLectura, Long>
{
    Optional<CamaraLectura> findByCamaraIdAndBucketStart(Long camaraId, Instant bucketStart);

    List<CamaraLectura> findByCamaraIdAndBucketStartAfterOrderByBucketStartAsc(Long camaraId, Instant since);

    List<CamaraLectura> findByCamaraIdOrderByBucketStartAsc(Long camaraId);

    @Query("SELECT cl FROM CamaraLectura cl WHERE cl.bucketStart = "
        + "(SELECT MAX(cl2.bucketStart) FROM CamaraLectura cl2 WHERE cl2.camara.id = cl.camara.id) "
        + "AND cl.camara.id IN :camaraIds")
    List<CamaraLectura> findUltimaPorCamaraIds(@Param("camaraIds") Collection<Long> camaraIds);
}
