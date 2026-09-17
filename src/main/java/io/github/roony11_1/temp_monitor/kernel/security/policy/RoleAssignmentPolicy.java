package io.github.roony11_1.temp_monitor.kernel.security.policy;

import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;

import java.util.Set;

/**
 * Política OCP para asignación y modificación de roles.
 * Abierto a extensión: agregar un nuevo rol solo requiere actualizar el mapa/privilegio,
 * sin modificar {@code UsuarioService}. Cerrado a modificación.
 */
public interface RoleAssignmentPolicy {

    /**
     * Valida si el actor puede asignar los roles solicitados al crear un usuario.
     */
    void assertCanAssignOnCreate(TokenUser actor, Set<Rol> requested, Long targetEmpresaId);

    /**
     * Valida si el actor puede modificar los roles / usuario objetivo.
     */
    void assertCanModify(TokenUser actor, Usuario target, Set<Rol> requested);

    /**
     * Valida si el actor puede asignar los roles en una actualización.
     */
    void assertCanAssignOnUpdate(TokenUser actor, Set<Rol> requested);

    /**
     * Indica si el actor tiene permiso general para crear usuarios (SUPER_ADMIN o ADMIN_EMPRESA).
     */
    void assertCanCreateUser(TokenUser actor);
}
