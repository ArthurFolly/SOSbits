package com.sosbits.helpdesk.config;

import com.sosbits.helpdesk.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // arquivos estáticos
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        // rotas públicas
                        .requestMatchers(
                                "/",
                                "/index",
                                "/login",
                                "/cadastro",
                                "/salvar",
                                "/error"
                        ).permitAll()

                        // qualquer outra rota
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username") // IMPORTANTE
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/?error")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    // 🔐 USUÁRIO + PERFIS VINDOS DO BANCO
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepository.findByEmail(username)
                .map(usuario -> User.builder()
                        .username(usuario.getEmail())
                        .password(usuario.getSenha())
                        .disabled(!usuario.getAtivo())
                        .authorities(
                                usuario.getPerfis().stream()
                                        .map(perfil -> "ROLE_" + perfil.getNome())
                                        .toArray(String[]::new)
                        )
                        .build()
                )
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuário não encontrado")
                );
    }

    // ⚠️ apenas para teste
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
