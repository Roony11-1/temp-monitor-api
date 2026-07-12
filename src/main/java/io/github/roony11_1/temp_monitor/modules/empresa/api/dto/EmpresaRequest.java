package io.github.roony11_1.temp_monitor.modules.empresa.api.dto;

import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import lombok.Data;

@Data
public class EmpresaRequest 
{
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;

    public Empresa fromRequest()
    {
        return Empresa.builder()
            .nombre(this.nombre)
            .direccion(this.direccion)
            .telefono(this.telefono)
            .email(this.email)
            .build();
    }
}
