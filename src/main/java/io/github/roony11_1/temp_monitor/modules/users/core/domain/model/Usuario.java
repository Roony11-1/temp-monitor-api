package io.github.roony11_1.temp_monitor.modules.users.core.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Usuario implements TokenUser 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Builder.Default
    private Instant createdAt = Instant.now();

    private Instant updatedAt;
    private Instant lastLogin;

    @Builder.Default
    private boolean activo = true;

    @Override
    public Long getEmpresaId() {
        return empresa != null ? empresa.getId() : null;
    }

    @Override
    public Long getSucursalId() {
        return sucursal != null ? sucursal.getId() : null;
    }
}
