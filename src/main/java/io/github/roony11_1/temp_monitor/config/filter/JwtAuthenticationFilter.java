package io.github.roony11_1.temp_monitor.config.filter;

import io.github.roony11_1.temp_monitor.kernel.security.jwt.JwtTokenProvider;
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
                List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getRoles(token).stream()
                        .map(SimpleGrantedAuthority::new)
                        .map(a -> new SimpleGrantedAuthority("ROLE_" + a.getAuthority()))
                        .toList();

                var authentication = new UsernamePasswordAuthenticationToken(
                        jwtTokenProvider.getTokenUser(token), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}
