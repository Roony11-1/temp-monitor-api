package io.github.roony11_1.temp_monitor.modules.camara.api.rest.admin;

import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.modules.camara.core.application.CompactionJobService;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.CompactionJob;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/compactacion")
@RequiredArgsConstructor
public class CompactacionController {

    private final CompactionJobService jobService;

    @PostMapping("/ejecutar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CompactionJob> ejecutar(@AuthenticationPrincipal TokenUser user) {
        // TokenUser viene del JwtAuthenticationFilter; fallback a SecurityContext si es null
        if (user == null) {
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof TokenUser tu) user = tu;
        }
        if (user == null) throw new io.github.roony11_1.temp_monitor.kernel.security.exception.NoAutenticadoException("Usuario no autenticado");
        CompactionJob job = jobService.crearJob(user);
        // Lanzar async vía proxy (controller -> service es cross-bean, aplica @Async)
        jobService.ejecutarAsync(job.getId());
        return ResponseEntity.accepted().body(job);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CompactionJob> estado(@PathVariable UUID id) {
        return jobService.obtener(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<CompactionJob> listar() {
        return jobService.listarUltimos(20);
    }
}
