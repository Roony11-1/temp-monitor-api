package io.github.roony11_1.temp_monitor.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class JwtConfig 
{
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-expiration-minutes:15}")
    private long accessExpirationMinutes;

    @Value("${app.jwt.refresh-expiration-days:7}")
    private long refreshExpirationDays;
}
