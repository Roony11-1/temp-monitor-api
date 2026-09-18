package io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository;

import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.CompactionJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompactionJobRepository extends JpaRepository<CompactionJob, UUID> {
}
