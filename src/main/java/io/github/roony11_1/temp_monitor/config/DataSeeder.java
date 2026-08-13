package io.github.roony11_1.temp_monitor.config;

import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.model.Camara;
import io.github.roony11_1.temp_monitor.modules.camara.core.domain.repository.CamaraRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Empresa;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.model.Sucursal;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.EmpresaRepository;
import io.github.roony11_1.temp_monitor.modules.empresa.core.domain.repository.SucursalRepository;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.model.Usuario;
import io.github.roony11_1.temp_monitor.modules.users.core.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner 
{
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;
    private final CamaraRepository camaraRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) 
    {
        limpiarRolObsoletoTecnico();

        if (usuarioRepository.count() > 0) return;

        log.info("Sembrando datos de prueba...");

        // Empresas
        var techCorp = empresaRepository.save(Empresa.builder().nombre("TechCorp").direccion("Av. Principal 123").telefono("555-0100").email("contacto@techcorp.com").build());
        var foodInc  = empresaRepository.save(Empresa.builder().nombre("FoodInc").direccion("Calle Secundaria 456").telefono("555-0200").email("contacto@foodinc.com").build());

        // Sucursales
        var centro = sucursalRepository.save(Sucursal.builder().nombre("Sucursal Centro").direccion("Calle 5ta 789").telefono("555-0101").empresa(techCorp).build());
        var norte  = sucursalRepository.save(Sucursal.builder().nombre("Sucursal Norte").direccion("Av. Norte 321").telefono("555-0102").empresa(techCorp).build());
        var sur    = sucursalRepository.save(Sucursal.builder().nombre("Sucursal Sur").direccion("Ruta 8 km 15").telefono("555-0201").empresa(foodInc).build());

        // Cámaras frigoríficas
        camaraRepository.save(Camara.builder().nombre("Cámara Frigorífica 1").descripcion("Cámara principal de congelados").sucursal(centro).temperaturaMin(-18.0).temperaturaMax(-15.0).build());
        camaraRepository.save(Camara.builder().nombre("Cámara Frigorífica 2").descripcion("Cámara de refrigerados").sucursal(centro).temperaturaMin(3.0).temperaturaMax(7.0).build());
        camaraRepository.save(Camara.builder().nombre("Cámara Frigorífica Norte").descripcion("Cámara de congelados").sucursal(norte).temperaturaMin(-18.0).temperaturaMax(-15.0).build());
        camaraRepository.save(Camara.builder().nombre("Cámara Frigorífica Sur").descripcion("Cámara de refrigerados y lácteos").sucursal(sur).temperaturaMin(3.0).temperaturaMax(7.0).build());

        var pass = "admin123";

        // Usuarios
        crearUsuario("admin@test.com",       pass, "Admin Global",      null,   null,   Rol.SUPER_ADMIN);
        crearUsuario("empresa1@test.com",    pass, "Admin TechCorp",    techCorp, null, Rol.ADMIN_EMPRESA);
        crearUsuario("empresa2@test.com",    pass, "Admin FoodInc",     foodInc,  null, Rol.ADMIN_EMPRESA);
        crearUsuario("sucursal1@test.com",   pass, "Admin Centro",      techCorp, centro, Rol.ADMIN_SUCURSAL);
        crearUsuario("sucursal2@test.com",   pass, "Admin Norte",       techCorp, norte,  Rol.ADMIN_SUCURSAL);
        crearUsuario("usuario1@test.com",    pass, "Usuario Centro",    techCorp, centro, Rol.USUARIO);
        crearUsuario("usuario2@test.com",    pass, "Usuario Norte",     techCorp, norte,  Rol.USUARIO);

        log.info("Seed completado. Todos los usuarios usan contraseña 'admin123'");
    }

    private void limpiarRolObsoletoTecnico() {
        int rolesBorrados = jdbcTemplate.update(
            "DELETE FROM usuario_roles WHERE rol = ?", "TECNICO");
        if (rolesBorrados > 0) {
            log.warn("Rol obsoleto TECNICO detectado: se borraron {} roles.", rolesBorrados);
        }

        int usuariosSinRol = jdbcTemplate.update(
            "DELETE FROM usuarios u WHERE NOT EXISTS (SELECT 1 FROM usuario_roles r WHERE r.usuario_id = u.id)");
        if (usuariosSinRol > 0) {
            log.warn("Se borraron {} usuarios que quedaron sin rol.", usuariosSinRol);
        }
    }

    private void crearUsuario(String email, String password, String nombre, Empresa empresa, Sucursal sucursal, Rol rol) {
        usuarioRepository.save(Usuario.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .nombre(nombre)
                .roles(Set.of(rol))
                .empresa(empresa)
                .sucursal(sucursal)
                .activo(true)
                .build());
    }
}
