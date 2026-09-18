package io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "sucursales")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sucursal 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String direccion;
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    @ToString.Exclude
    private Empresa empresa;

    @Builder.Default
    private boolean activo = true;

    private Instant deletedAt;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public void actualizar(String nombre, String direccion, String telefono, Empresa empresa) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        if (empresa != null) this.empresa = empresa;
    }

    public void reasignarEmpresa(Empresa empresa) {
        if (empresa == null) throw new IllegalArgumentException("Empresa no puede ser null");
        if (empresa.getDeletedAt() != null) throw new IllegalArgumentException("Empresa eliminada");
        this.empresa = empresa;
    }
}
