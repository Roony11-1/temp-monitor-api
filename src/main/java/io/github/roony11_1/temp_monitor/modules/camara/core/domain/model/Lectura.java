package io.github.roony11_1.temp_monitor.modules.camara.core.domain.model;

import java.time.Instant;
import java.util.UUID;

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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lecturas")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lectura 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID sensorUuid;

    @Column(nullable = false)
    private Double temperatura;

    @CreationTimestamp
    private Instant timestamp;
}
