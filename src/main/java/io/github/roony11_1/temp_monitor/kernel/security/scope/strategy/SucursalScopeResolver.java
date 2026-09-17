package io.github.roony11_1.temp_monitor.kernel.security.scope.strategy;

import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(2)
public class SucursalScopeResolver implements ScopeStrategyResolver {
    @Override
    public Optional<ScopeStrategy> resolve(TokenUser user) {
        if (user.sucursalId() != null) {
            return Optional.of(new SucursalScopeStrategy(user.sucursalId(), user.empresaId()));
        }
        return Optional.empty();
    }
}
