package io.github.roony11_1.temp_monitor.modules.camara.core.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "camara_lecturas", uniqueConstraints = @UniqueConstraint(name = "uk_camara_lecturas_bucket", columnNames = {"camara_id", "bucket_start"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CamaraLectura 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camara_id", nullable = false)
    @ToString.Exclude
    private Camara camara;

    @Column(name = "bucket_start", nullable = false)
    private Instant bucketStart;

    @Column(name = "muestreado_en", nullable = false)
    private Instant muestreadoEn;

    private Double promedio;

    @Column(name = "conteo_sensores")
    private int conteoSensores;
}
