package io.github.roony11_1.temp_monitor.modules.camara.core.domain.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Agregado (rollup) de muestras de una cámara, generado por la compactación.
 *
 * <p>Un bucket por {@code camara}, {@code granularidad} y {@code bucketStart}
 * (inicio del día/mes en UTC). Guarda promedio, mínimo, máximo y conteo de las
 * muestras de {@code CamaraLectura} (o de los DAILY) que reemplaza.
 */
@Entity
@Table(name = "camara_lecturas_resumen", uniqueConstraints = {
    @UniqueConstraint(name = "uk_camara_lecturas_resumen_bucket", columnNames = {"camara_id", "granularidad", "bucket_start"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CamaraLecturaResumen
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camara_id", nullable = false)
    @ToString.Exclude
    private Camara camara;

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