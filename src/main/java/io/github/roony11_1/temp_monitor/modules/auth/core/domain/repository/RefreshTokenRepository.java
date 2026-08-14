package io.github.roony11_1.temp_monitor.modules.auth.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.auth.core.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>
{
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUserId(Long userId);
}