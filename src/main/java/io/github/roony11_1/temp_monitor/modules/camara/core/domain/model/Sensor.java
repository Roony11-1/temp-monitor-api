package io.github.roony11_1.temp_monitor.modules.camara.core.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "sensores")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sensor 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String apiKeyHash;

    @Column(nullable = false, unique = true)
    private String macAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camara_id")
    @ToString.Exclude
    private Camara camara;

    private Instant ultimoContacto;

    @Builder.Default
    private EstadoSensor estado = EstadoSensor.PENDIENTE;

    /**
     * Estado anterior guardado por la cascada al deshabilitar el sensor.
     * {@code null} = deshabilitado manualmente (no se reactiva en cascada);
     * un valor distinto = deshabilitado por la cascada (se restaura al activar).
     */
    private EstadoSensor estadoPrevio;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant deletedAt;

    /**
     * Indica si el sensor puede registrar lecturas.
     * Delega la decisión al {@link EstadoSensor} (OCP) y exige cámara asignada.
     * Corrige bug previo donde el nombre invertía la semántica (retornaba true cuando NO podía).
     */
    public boolean puedeRegistrarLectura()
    {
        if (this.getCamara() == null)
        {
            return false;
        }
        return this.getEstado() != null && this.getEstado().canRecord();
    }

    /**
     * Conveniencia: negación de {@link #puedeRegistrarLectura()} para callers que validan bloqueo.
     */
    public boolean isBlockedForRecording()
    {
        return !puedeRegistrarLectura();
    }

    // ===== Métodos de dominio rico =====

    /**
     * Asigna el sensor a una cámara y activa automáticamente.
     * Encapsula invariante: cámara no eliminada y transición a ACTIVO.
     */
    public void asignarACamara(Camara camara) {
        if (camara == null) throw new IllegalArgumentException("Camara no puede ser null");
        if (camara.getDeletedAt() != null) throw new IllegalArgumentException("Camara eliminada");
        this.camara = camara;
        this.estado = EstadoSensor.ACTIVO;
        this.estadoPrevio = null;
    }

    /**
     * Cambia el estado validando que no se asigne PENDIENTE manualmente (OCP vía enum).
     */
    public void cambiarEstado(EstadoSensor nuevoEstado) {
        if (nuevoEstado == null) throw new IllegalArgumentException("Estado no puede ser null");
        if (!nuevoEstado.canBeAssignedManually()) {
            throw new IllegalArgumentException("No se puede asignar el estado " + nuevoEstado + " manualmente");
        }
        this.estado = nuevoEstado;
        this.estadoPrevio = null;
    }

    /**
     * Reasignación con lógica de negocio: si estaba PENDIENTE y no se especifica estado nuevo,
     * se activa automáticamente (usado en actualización con cambio de cámara).
     */
    public void reasignarSiPendiente(Camara nuevaCamara, EstadoSensor estadoSolicitado) {
        this.camara = nuevaCamara;
        if (this.estado != null && this.estado.isPending() && estadoSolicitado == null) {
            this.estado = EstadoSensor.ACTIVO;
            this.estadoPrevio = null;
        }
        if (estadoSolicitado != null) {
            cambiarEstado(estadoSolicitado);
        }
    }

    public void rotarApiKey(String newHash) {
        this.apiKeyHash = newHash;
    }

    public void marcarEliminado(Instant ahora) {
        this.deletedAt = ahora;
    }

    public void restaurar() {
        this.deletedAt = null;
    }
}
