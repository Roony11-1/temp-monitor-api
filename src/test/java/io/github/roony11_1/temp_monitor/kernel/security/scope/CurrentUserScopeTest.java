package io.github.roony11_1.temp_monitor.kernel.security.scope;

import io.github.roony11_1.temp_monitor.kernel.security.exception.AccesoDenegadoException;
import io.github.roony11_1.temp_monitor.kernel.security.exception.NoAutenticadoException;
import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.kernel.specification.FilterCondition;
import io.github.roony11_1.temp_monitor.kernel.specification.FilterOperator;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentUserScopeTest {

    private final CurrentUserScope scope = new CurrentUserScope();

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void superAdminNoRecibeCondicionDeAmbitoYAccedeATodo() {
        authenticate(usuario(Rol.SUPER_ADMIN, null, null));

        assertThat(scope.scopeCondition("empresa.id", "sucursal.id")).isEmpty();
        assertThat(scope.canAccess(null, null)).isTrue();
        assertThat(scope.canAccess(999L, 888L)).isTrue();
    }

    @Test
    void adminEmpresaRecibeCondicionPorEmpresa() {
        authenticate(usuario(Rol.ADMIN_EMPRESA, 7L, null));

        Optional<FilterCondition> condition = scope.scopeCondition("empresa.id", "sucursal.id");

        assertThat(condition).isPresent();
        assertThat(condition.get().getField()).isEqualTo("empresa.id");
        assertThat(condition.get().getOperator()).isEqualTo(FilterOperator.EQ);
        assertThat(condition.get().getValue()).isEqualTo(7L);
    }

    @Test
    void adminSucursalRecibeCondicionPorSucursal() {
        authenticate(usuario(Rol.ADMIN_SUCURSAL, 7L, 11L));

        Optional<FilterCondition> condition = scope.scopeCondition("empresa.id", "sucursal.id");

        assertThat(condition).isPresent();
        assertThat(condition.get().getField()).isEqualTo("sucursal.id");
        assertThat(condition.get().getValue()).isEqualTo(11L);
    }

    @Test
    void canAccessPorEmpresaSoloCoincideConSuEmpresa() {
        authenticate(usuario(Rol.ADMIN_EMPRESA, 7L, null));

        assertThat(scope.canAccess(1L, 7L)).isTrue();
        assertThat(scope.canAccess(1L, 8L)).isFalse();
    }

    @Test
    void canAccessPorSucursalSoloCoincideConSuSucursal() {
        authenticate(usuario(Rol.ADMIN_SUCURSAL, 7L, 11L));

        assertThat(scope.canAccess(11L, 7L)).isTrue();
        assertThat(scope.canAccess(12L, 7L)).isFalse();
    }

    @Test
    void sensorSinCamaraSoloAccesibleParaSuperAdmin() {
        authenticate(usuario(Rol.ADMIN_EMPRESA, 7L, null));

        assertThat(scope.canAccess(null, null)).isFalse();
    }

    @Test
    void usuarioSinAmbitoLanzaAccessDenied() {
        authenticate(usuario(Rol.USUARIO, null, null));

        assertThatThrownBy(() -> scope.scopeCondition("empresa.id", "sucursal.id"))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void sinUsuarioAutenticadoLanzaNoAutenticado() {
        assertThatThrownBy(scope::isSuperAdmin)
                .isInstanceOf(NoAutenticadoException.class);
    }

    private void authenticate(TokenUser principal) {
        var authorities = principal.getRoles().stream()
                .map(rol -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + rol.name()))
                .toList();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, authorities));
    }

    private Usuario usuario(Rol rol, Long empresaId, Long sucursalId) {
        Empresa empresa = empresaId != null ? Empresa.builder().id(empresaId).build() : null;
        Sucursal sucursal = sucursalId != null ? Sucursal.builder().id(sucursalId).build() : null;
        return Usuario.builder()
                .roles(Set.of(rol))
                .empresa(empresa)
                .sucursal(sucursal)
                .build();
    }
}