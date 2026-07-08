package io.github.roony11_1.temp_monitor.modules.camara.core.domain.model;

import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "camaras")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Camara 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    @ToString.Exclude
    private Sucursal sucursal;

    private Double temperaturaMinima;
    private Double temperaturaMaxima;

    @Builder.Default
    private boolean activo = true;

    @Builder.Default
    private Instant createdAt = Instant.now();

    private Instant updatedAt;
}
