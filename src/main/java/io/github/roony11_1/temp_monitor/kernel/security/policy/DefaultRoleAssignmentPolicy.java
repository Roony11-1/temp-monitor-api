package io.github.roony11_1.temp_monitor.kernel.security.policy;

import io.github.roony11_1.temp_monitor.kernel.security.exception.AccesoDenegadoException;
import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Implementación por defecto basada en mapa configurable.
 * OCP: nuevo rol = nueva entrada en {@code ALLOWED_TO_ASSIGN} o nuevo valor de {@code Rol.isPrivileged()}.
 * No requiere tocar {@code UsuarioService}.
 */
@Component
public class DefaultRoleAssignmentPolicy implements RoleAssignmentPolicy {

    /**
     * Define qué roles puede asignar cada actor.
     * SUPER_ADMIN -> todos; ADMIN_EMPRESA -> solo ADMIN_SUCURSAL y USUARIO.
     * USUARIO / ADMIN_SUCURSAL no pueden crear usuarios.
     */
    private static final Map<Rol, Set<Rol>> ALLOWED_TO_ASSIGN = Map.of(
            Rol.SUPER_ADMIN, Set.of(Rol.SUPER_ADMIN, Rol.ADMIN_EMPRESA, Rol.ADMIN_SUCURSAL, Rol.USUARIO),
            Rol.ADMIN_EMPRESA, Set.of(Rol.ADMIN_SUCURSAL, Rol.USUARIO)
    );

    @Override
    public void assertCanCreateUser(TokenUser actor) {
        if (isSuperAdmin(actor)) return;
        if (actor.roles().contains(Rol.ADMIN_EMPRESA)) return;
        throw new AccesoDenegadoException("No tienes permiso para crear usuarios");
    }

    @Override
    public void assertCanAssignOnCreate(TokenUser actor, Set<Rol> requested, Long targetEmpresaId) {
        if (isSuperAdmin(actor)) {
            return; // SUPER_ADMIN puede asignar cualquier rol y empresa
        }
        if (actor.roles().contains(Rol.ADMIN_EMPRESA)) {
            assertAllowedRoles(actor, requested);
            assertSameEmpresa(actor, targetEmpresaId);
            return;
        }
        throw new AccesoDenegadoException("No tienes permiso para crear usuarios");
    }

    @Override
    public void assertCanAssignOnUpdate(TokenUser actor, Set<Rol> requested) {
        if (isSuperAdmin(actor)) return;
        assertAllowedRoles(actor, requested);
    }

    @Override
    public void assertCanModify(TokenUser actor, Usuario target, Set<Rol> requested) {
        if (isSuperAdmin(actor)) return;
        // No puede modificar usuarios privilegiados
        if (Rol.containsPrivileged(target.getRoles())) {
            throw new AccesoDenegadoException("No puedes modificar un usuario SUPER_ADMIN o ADMIN_EMPRESA");
        }
        // Valida roles que intenta asignar
        assertCanAssignOnUpdate(actor, requested);
    }

    private void assertAllowedRoles(TokenUser actor, Set<Rol> requested) {
        if (requested == null || requested.isEmpty()) return;
        // Determina el rol primario del actor (SUPER_ADMIN tiene prioridad)
        Rol actorPrimary = isSuperAdmin(actor) ? Rol.SUPER_ADMIN : Rol.ADMIN_EMPRESA;
        Set<Rol> allowed = ALLOWED_TO_ASSIGN.getOrDefault(actorPrimary, Set.of());
        for (Rol rol : requested) {
            if (!allowed.contains(rol)) {
                throw new AccesoDenegadoException("No puedes asignar el rol " + rol);
            }
        }
    }

    private void assertSameEmpresa(TokenUser actor, Long targetEmpresaId) {
        if (targetEmpresaId == null || !targetEmpresaId.equals(actor.empresaId())) {
            throw new AccesoDenegadoException("Solo puedes crear usuarios en tu propia empresa");
        }
    }

    private boolean isSuperAdmin(TokenUser actor) {
        return actor.roles().contains(Rol.SUPER_ADMIN);
    }
}
