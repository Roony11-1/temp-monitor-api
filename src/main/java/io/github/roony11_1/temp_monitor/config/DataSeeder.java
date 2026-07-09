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

    @Override
    public void run(String... args) 
    {
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
        camaraRepository.save(Camara.builder().nombre("Cámara Frigorífica 1").descripcion("Cámara principal de congelados").sucursal(centro).build());
        camaraRepository.save(Camara.builder().nombre("Cámara Frigorífica 2").descripcion("Cámara de refrigerados").sucursal(centro).build());
        camaraRepository.save(Camara.builder().nombre("Cámara Frigorífica Norte").descripcion("Cámara de congelados").sucursal(norte).build());
        camaraRepository.save(Camara.builder().nombre("Cámara Frigorífica Sur").descripcion("Cámara de refrigerados y lácteos").sucursal(sur).build());

        var pass = "admin123";

        // Usuarios
        crearUsuario("admin@test.com",       pass, "Admin Global",      null,   null,   Rol.SUPER_ADMIN);
        crearUsuario("empresa1@test.com",    pass, "Admin TechCorp",    techCorp.getId(), null, Rol.ADMIN_EMPRESA);
        crearUsuario("empresa2@test.com",    pass, "Admin FoodInc",     foodInc.getId(),  null, Rol.ADMIN_EMPRESA);
        crearUsuario("sucursal1@test.com",   pass, "Admin Centro",      techCorp.getId(), centro.getId(), Rol.ADMIN_SUCURSAL);
        crearUsuario("sucursal2@test.com",   pass, "Admin Norte",       techCorp.getId(), norte.getId(),  Rol.ADMIN_SUCURSAL);
        crearUsuario("tecnico1@test.com",    pass, "Técnico Centro",    techCorp.getId(), centro.getId(), Rol.TECNICO);
        crearUsuario("tecnico2@test.com",    pass, "Técnico Norte",     techCorp.getId(), norte.getId(),  Rol.TECNICO);
        crearUsuario("tecnico3@test.com",    pass, "Técnico Sur",       foodInc.getId(),  sur.getId(),    Rol.TECNICO);
        crearUsuario("usuario1@test.com",    pass, "Usuario Centro",    techCorp.getId(), centro.getId(), Rol.USUARIO);
        crearUsuario("usuario2@test.com",    pass, "Usuario Norte",     techCorp.getId(), norte.getId(),  Rol.USUARIO);

        log.info("Seed completado. Todos los usuarios usan contraseña 'admin123'");
    }

    private void crearUsuario(String email, String password, String nombre, Long empresaId, Long sucursalId, Rol rol) {
        usuarioRepository.save(Usuario.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .nombre(nombre)
                .roles(Set.of(rol))
                .empresaId(empresaId)
                .sucursalId(sucursalId)
                .activo(true)
                .build());
    }
}
