package io.github.roony11_1.temp_monitor.modules.camara.core.domain.model;

import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "camaras")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Camara 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    private Double temperaturaMin;

    private Double temperaturaMax;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    @ToString.Exclude
    private Sucursal sucursal;

    @OneToMany(mappedBy = "camara", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private final List<Sensor> sensores = new ArrayList<>();

    @Builder.Default
    private boolean activo = true;

    private Instant deletedAt;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    // ===== Métodos de dominio rico =====

    /**
     * Actualiza datos básicos y rango de temperatura con validación centralizada.
     * OCP: validación vive en {@link io.github.roony11_1.temp_monitor.kernel.domain.RangoTemperatura},
     * no dispersa en Services.
     */
    public void actualizar(String nombre, String descripcion, Double temperaturaMin, Double temperaturaMax, Sucursal nuevaSucursal) {
        io.github.roony11_1.temp_monitor.kernel.domain.RangoTemperatura.validar(temperaturaMin, temperaturaMax);
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.temperaturaMin = temperaturaMin;
        this.temperaturaMax = temperaturaMax;
        if (nuevaSucursal != null) {
            this.sucursal = nuevaSucursal;
        }
    }

    public void cambiarRango(Double min, Double max) {
        io.github.roony11_1.temp_monitor.kernel.domain.RangoTemperatura.validar(min, max);
        this.temperaturaMin = min;
        this.temperaturaMax = max;
    }

    public void reasignarSucursal(Sucursal sucursal) {
        if (sucursal == null) throw new IllegalArgumentException("Sucursal no puede ser null");
        if (sucursal.getDeletedAt() != null) throw new IllegalArgumentException("Sucursal eliminada");
        this.sucursal = sucursal;
    }
}
