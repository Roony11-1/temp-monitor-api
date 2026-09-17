package io.github.roony11_1.temp_monitor.kernel.security.scope.strategy;

import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(1)
public class SuperAdminScopeResolver implements ScopeStrategyResolver {
    @Override
    public Optional<ScopeStrategy> resolve(TokenUser user) {
        if (user.roles().contains(Rol.SUPER_ADMIN)) {
            return Optional.of(new SuperAdminScopeStrategy());
        }
        return Optional.empty();
    }
}
