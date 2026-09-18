package io.github.roony11_1.temp_monitor.modules.users.core.domain.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();

    private String nombre;
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    @ToString.Exclude
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    @ToString.Exclude
    private Sucursal sucursal;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant lastLogin;

    @Builder.Default
    private boolean activo = true;

    private Instant deletedAt;

    public Long getEmpresaId() {
        return empresa != null ? empresa.getId() : null;
    }

    public Long getSucursalId() {
        return sucursal != null ? sucursal.getId() : null;
    }

    public void actualizarPerfil(String nombre, String telefono, Empresa empresa, Sucursal sucursal, Set<Rol> roles) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.empresa = empresa;
        this.sucursal = sucursal;
        if (roles != null && !roles.isEmpty()) this.roles = roles;
    }

    public void cambiarPassword(String hash) { this.passwordHash = hash; }
    public void desactivar() { this.activo = false; }
    public void activar() { this.activo = true; }
    public void marcarEliminado(Instant ahora) { this.deletedAt = ahora; }
    public void restaurar() { this.deletedAt = null; }
}
