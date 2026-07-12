package io.github.roony11_1.temp_monitor.modules.auth.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse 
{
    private String token;
}
