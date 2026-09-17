package io.github.roony11_1.temp_monitor.kernel.security.scope.strategy;

import io.github.roony11_1.temp_monitor.kernel.security.exception.AccesoDenegadoException;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory OCP que delega la resolución a una cadena de {@link ScopeStrategyResolver}s.
 * Nuevo tipo de ámbito = nuevo bean resolver, sin tocar este factory.
 */
@Component
@RequiredArgsConstructor
public class ScopeStrategyFactory {

    private final List<ScopeStrategyResolver> resolvers;

    public ScopeStrategy resolve(TokenUser user) {
        for (ScopeStrategyResolver resolver : resolvers) {
            var strategy = resolver.resolve(user);
            if (strategy.isPresent()) {
                return strategy.get();
            }
        }
        throw new AccesoDenegadoException("El usuario no tiene un ámbito de acceso asignado");
    }
}
