package io.github.roony11_1.temp_monitor.kernel.security.scope.strategy;

import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;

import java.util.Optional;

/**
 * Resuelve si un {@link TokenUser} corresponde a una estrategia concreta.
 * OCP: agregar un nuevo tipo de ámbito implica registrar un nuevo bean que implemente esta interfaz,
 * sin modificar el factory existente (Chain of Responsibility).
 */
public interface ScopeStrategyResolver {
    Optional<ScopeStrategy> resolve(TokenUser user);
}
