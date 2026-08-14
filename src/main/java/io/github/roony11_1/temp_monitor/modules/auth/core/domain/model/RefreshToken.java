package io.github.roony11_1.temp_monitor.modules.auth.core.domain.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Token de renovación (refresh) persistido en BD para poder revocarlo.
 * Solo se guarda su hash SHA-256 ({@code tokenHash}); el token en claro se
 * entrega una única vez al cliente. Cada renovación rota: se revoca el usado
 * y se emite uno nuevo.
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RefreshToken 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private Long userId;

    @CreationTimestamp
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant revokedAt;

    public boolean isRevoked()
    {
        return revokedAt != null;
    }

    public boolean isExpired(Instant now)
    {
        return now.isAfter(expiresAt);
    }
}