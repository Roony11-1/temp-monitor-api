package io.github.roony11_1.temp_monitor.modules.camara.core.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "compaction_jobs")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompactionJob {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCompactionJob estado;

    @CreationTimestamp
    private Instant iniciadoEn;

    private Instant terminadoEn;

    // Quien lo ejecutó
    private Long ejecutadoPorId;
    private String ejecutadoPorEmail;

    // Resultados
    private Integer totalDiarios;
    private Integer totalMensuales;
    private Integer totalPurgados;

    @Column(columnDefinition = "TEXT")
    private String detalleJson; // JSON con por handler: [{name, diarios, mensuales, purgados}]

    @Column(columnDefinition = "TEXT")
    private String error;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (iniciadoEn == null) iniciadoEn = Instant.now();
        if (estado == null) estado = EstadoCompactionJob.RUNNING;
    }
}
