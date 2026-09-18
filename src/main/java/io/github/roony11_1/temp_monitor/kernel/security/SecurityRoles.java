package io.github.roony11_1.temp_monitor.kernel.security;

/**
 * Constantes centralizadas para roles. Evita literals {@code "SUPER_ADMIN"} / {@code "ADMIN_EMPRESA"}
 * duplicados en {@code @PreAuthorize}, {@code SecurityConfig} y Services.
 * OCP: renombrar un rol solo requiere cambiar aquí.
 */
public final class SecurityRoles {
    private SecurityRoles() {}

    public static final String SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ADMIN_EMPRESA = "ADMIN_EMPRESA";
    public static final String ADMIN_SUCURSAL = "ADMIN_SUCURSAL";
    public static final String USUARIO = "USUARIO";
    public static final String SENSOR = "SENSOR";

    public static final String HAS_SUPER_ADMIN = "hasRole('SUPER_ADMIN')";
    public static final String HAS_ADMIN_EMPRESA_OR_SUPER = "hasAnyRole('ADMIN_EMPRESA','SUPER_ADMIN')";
    public static final String HAS_ANY_ADMIN = "hasAnyRole('ADMIN_EMPRESA','ADMIN_SUCURSAL','SUPER_ADMIN')";
    public static final String HAS_SUCURSAL_ADMIN_OR_ABOVE = "hasAnyRole('ADMIN_EMPRESA','ADMIN_SUCURSAL','SUPER_ADMIN')";
}
