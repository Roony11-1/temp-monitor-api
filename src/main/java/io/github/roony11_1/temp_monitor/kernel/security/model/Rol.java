package io.github.roony11_1.temp_monitor.kernel.security.model;

import java.util.Set;

public enum Rol 
{
    SUPER_ADMIN(true),
    ADMIN_EMPRESA(true),
    ADMIN_SUCURSAL(false),
    USUARIO(false);

    private final boolean privileged;

    Rol(boolean privileged) {
        this.privileged = privileged;
    }

    /**
     * Indica si el rol es privilegiado (requiere SUPER_ADMIN para asignar/modificar).
     * OCP: agregar un nuevo rol privilegiado solo requiere marcarlo aquí como privileged=true,
     * sin modificar checks dispersos en Services.
     */
    public boolean isPrivileged() {
        return privileged;
    }

    /**
     * Conveniencia OCP para chequear si el conjunto contiene algún rol privilegiado.
     */
    public static boolean containsPrivileged(Set<Rol> roles) {
        if (roles == null) return false;
        return roles.stream().anyMatch(Rol::isPrivileged);
    }
}
