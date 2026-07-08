package io.github.roony11_1.temp_monitor.kernel.security.model;

import java.util.Set;

public interface TokenUser 
{
    Long getId();
    String getEmail();
    Set<Rol> getRoles();
    Long getEmpresaId();
    Long getSucursalId();
}
