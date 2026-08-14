package io.github.roony11_1.temp_monitor.config.filter;

import io.github.roony11_1.temp_monitor.kernel.security.jwt.JwtTokenProvider;
import io.github.roony11_1.temp_monitor.kernel.security.model.Rol;
import io.github.roony11_1.temp_monitor.kernel.security.model.TokenUser;
import io.github.roony11_1.temp_monitor.kernel.security.service.IUserCredentialsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter 
{
    private final JwtTokenProvider jwtTokenProvider;
    private final IUserCredentialsService userCredentialsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException 
    {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) 
        {
            String token = header.substring(7);

            if (jwtTokenProvider.validateToken(token)) 
            {
                try 
                {
                    // Validación por request contra BD: el JWT deja de valer si el
                    // usuario (o su empresa/sucursal) fue eliminado/desactivado.
                    TokenUser tokenUser = userCredentialsService.validateAndGetByUserId(
                            jwtTokenProvider.getUserId(token));

                    List<SimpleGrantedAuthority> authorities = tokenUser.roles().stream()
                            .map(Rol::name)
                            .map(name -> new SimpleGrantedAuthority("ROLE_" + name))
                            .toList();

                    var authentication = new UsernamePasswordAuthenticationToken(tokenUser, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } 
                catch (Exception e) 
                {
                    SecurityContextHolder.clearContext();
                }
            }
        }

        chain.doFilter(request, response);
    }
}
