package io.github.roony11_1.temp_monitor.modules.auth.api.dto;

import lombok.Data;

@Data
public class RefreshRequest 
{
    private String refreshToken;
}