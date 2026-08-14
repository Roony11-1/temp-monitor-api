package io.github.roony11_1.temp_monitor.modules.camara.core.domain.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agregado (rollup) de lecturas de un sensor, generado por la compactación.
 *
 * <p>Un bucket por {@code sensorUuid}, {@code granularidad} y {@code bucketStart}
 * (inicio del día/mes en UTC). Guarda promedio, mínimo, máximo y conteo de las
 * lecturas crudas (o de los DAILY) que reemplaza. Vive aparte de {@code Lectura}:
 * esa tabla sigue siendo solo el write-path de los sensores.
 */
@Entity
@Table(name = "lecturas_resumen", uniqueConstraints = {
    @UniqueConstraint(name = "uk_lecturas_resumen_bucket", columnNames = {"sensor_uuid", "granularidad", "bucket_start"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LecturaResumen 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_uuid", nullable = false)
    private UUID sensorUuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GranularidadLectura granularidad;

    @Column(name = "bucket_start", nullable = false)
    private Instant bucketStart;

    private Double promedio;

    private Double minimo;

    private Double maximo;

    @Column(nullable = false)
    private Integer conteo;

    @CreationTimestamp
    @Column(name = "actualizado_en")
    private Instant actualizadoEn;
}