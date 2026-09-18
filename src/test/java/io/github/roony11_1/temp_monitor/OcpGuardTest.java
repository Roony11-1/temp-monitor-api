package io.github.roony11_1.temp_monitor;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guard OCP: evita regresiones de Abierto/Cerrado.
 * Falla si se reintroduce {@code switch} sobre {@code EstadoSensor}, {@code Rol} o {@code GranularidadLectura}
 * fuera de los lugares permitidos (los enums mismos y sus strategies).
 * Para agregar un nuevo estado/rol/granularidad debe extenderse el enum o una strategy, no agregar un case.
 */
class OcpGuardTest {

    private static final Path SRC = Paths.get("src/main/java");

    @Test
    void noSwitchSobreEstadoSensorRolOGranularidadFueraDeEnums() throws IOException {
        try (Stream<Path> walk = Files.walk(SRC)) {
            List<String> violations = walk
                    .filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !isAllowedToContainSwitch(p))
                    .flatMap(p -> {
                        try {
                            List<String> lines = Files.readAllLines(p);
                            return lines.stream()
                                    .filter(l -> {
                                        String t = l.trim();
                                        if (t.startsWith("//") || t.startsWith("*")) return false;
                                        return l.contains("switch") && (l.contains("EstadoSensor") || l.contains("Rol") || l.contains("Granularidad"));
                                    })
                                    .map(l -> p + ":" + l.trim());
                        } catch (IOException e) {
                            return Stream.empty();
                        }
                    })
                    .toList();

            // También prohíbe switches genéricos que contengan cases de esos enums (más laxo)
            try (Stream<Path> walk2 = Files.walk(SRC)) {
                List<String> caseViolations = walk2
                        .filter(p -> p.toString().endsWith(".java"))
                        .filter(p -> !isAllowedToContainSwitch(p))
                        .flatMap(p -> {
                            try {
                                String content = Files.readString(p);
                                // Ignora comentarios de línea que mencionan "sin switch"
                                String codeOnly = content.lines()
                                        .filter(l -> !l.trim().startsWith("//"))
                                        .collect(java.util.stream.Collectors.joining("\n"));
                                if (codeOnly.contains("switch") && (codeOnly.contains("case PENDIENTE") || codeOnly.contains("case ACTIVO") || codeOnly.contains("case DESHABILITADO") || codeOnly.contains("case DAILY") || codeOnly.contains("case MONTHLY") || codeOnly.contains("case SUPER_ADMIN"))) {
                                    return Stream.of(p + ": contiene switch con case de EstadoSensor/Rol/Granularidad");
                                }
                                return Stream.empty();
                            } catch (IOException e) {
                                return Stream.empty();
                            }
                        })
                        .toList();
                violations = Stream.concat(violations.stream(), caseViolations.stream()).toList();
            }

            assertThat(violations)
                    .as("Se detectaron switches que violan OCP (agregar nuevo estado/rol debe hacerse extendiendo enum/strategy, no con switch). Violaciones: %s", violations)
                    .isEmpty();
        }
    }

    @Test
    void noUsoDirectoDeWithAliasesFueraDeAdapter() throws IOException {
        try (Stream<Path> walk = Files.walk(SRC)) {
            List<String> violations = walk
                    .filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.toString().endsWith("FilterParserAdapter.java"))
                    .flatMap(p -> {
                        try {
                            return Files.readAllLines(p).stream()
                                    .filter(l -> l.contains("withAliases"))
                                    .map(l -> p + ":" + l.trim());
                        } catch (IOException e) {
                            return Stream.empty();
                        }
                    })
                    .toList();
            assertThat(violations)
                    .as("withAliases fue removido en 1.1.0, usar FilterParserAdapter. Violaciones: %s", violations)
                    .isEmpty();
        }
    }

    private boolean isAllowedToContainSwitch(Path p) {
        String s = p.toString().replace("\\", "/");
        // Los enums mismos y sus strategies pueden contener lógica de estado; pero actualmente no usan switch
        return s.contains("/EstadoSensor.java") || s.contains("/Rol.java") || s.contains("/GranularidadLectura.java");
    }
}
