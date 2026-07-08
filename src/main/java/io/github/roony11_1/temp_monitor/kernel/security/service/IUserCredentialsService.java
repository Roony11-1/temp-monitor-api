package io.github.roony11_1.temp_monitor.kernel.security.service;

import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;

public interface IUserCredentialsService 
{
    TokenUser authenticate(String email, String rawPassword);
}
