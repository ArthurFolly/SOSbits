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
                // ❌ CSRF desabilitado só pra facilitar no MVC
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // ✅ ARQUIVOS ESTÁTICOS
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        // ✅ ROTAS PÚBLICAS
                        .requestMatchers(
                                "/",
                                "/index",
                                "/login",
                                "/cadastro",
                                "/salvar",
                                "/error"
                        ).permitAll()

                        // ✅ ADMIN (LIBERADO POR ENQUANTO)
                        .requestMatchers("/admin/**").permitAll()

                        // 🔒 TODO O RESTO PRECISA ESTAR LOGADO
                        .anyRequest().authenticated()
                )

                // ✅ LOGIN
                .formLogin(form -> form
                        .loginPage("/")                // tela de login
                        .loginProcessingUrl("/login")  // POST do login
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/?error")
                        .permitAll()
                )

                // ✅ LOGOUT
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    // 🔐 BUSCA USUÁRIO NO BANCO
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> usuarioRepository.findByEmail(email)
                .map(u -> new User(
                        u.getEmail(),
                        u.getSenha(),
                        new ArrayList<>() // sem roles por enquanto
                ))
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuário não encontrado: " + email)
                );
    }

    // ⚠️ APENAS PARA TESTES
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
