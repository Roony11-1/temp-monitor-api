package io.github.roony11_1.temp_monitor.modules.auth.api.rest;

import io.github.roony11_1.temp_monitor.modules.auth.api.dto.LoginRequest;
import io.github.roony11_1.temp_monitor.modules.auth.api.dto.LoginResponse;
import io.github.roony11_1.temp_monitor.modules.auth.core.application.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController 
{
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) 
    {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}
