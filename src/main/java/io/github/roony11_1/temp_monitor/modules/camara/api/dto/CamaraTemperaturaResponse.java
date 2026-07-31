package io.github.roony11_1.temp_monitor.modules.camara.api.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CamaraTemperaturaResponse 
{
    private Double promedio;
    private long sensoresConDatos;
    private Instant ultimaLectura;
}
