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

import java.util.ArrayList;

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
                        // ✅ LIBERA TUDO QUE É ESTÁTICO (CSS/JS/IMAGENS/WEBJARS/FAVICON)
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        // ✅ LIBERA ROTAS PÚBLICAS (LOGIN/CADASTRO/ERRO)
                        .requestMatchers(
                                "/",
                                "/index",
                                "/login",
                                "/cadastro",
                                "/salvar",
                                "/error"
                        ).permitAll()

                        // 🔒 O RESTO É PROTEGIDO
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/")               // sua tela de login
                        .loginProcessingUrl("/login") // endpoint do POST do login
                        .defaultSuccessUrl("/chamados/dashboard", true) // ✅ ajuste aqui
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

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> usuarioRepository.findByEmail(email)
                .map(u -> new User(
                        u.getEmail(),
                        u.getSenha(),
                        new ArrayList<>()
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // ⚠️ Só para testes (senha em texto puro). Depois trocamos para BCrypt.
        return NoOpPasswordEncoder.getInstance();
    }
}
