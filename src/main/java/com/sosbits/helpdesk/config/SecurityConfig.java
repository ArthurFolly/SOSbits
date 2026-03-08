package com.sosbits.helpdesk.config;

import com.sosbits.helpdesk.repository.UsuarioRepository;
import com.sosbits.helpdesk.security.LoginSuccessHandler;
import com.sosbits.helpdesk.security.LogoutHandlerCustom;
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
    private final LoginSuccessHandler loginSuccessHandler;
    private final LogoutHandlerCustom logoutHandlerCustom;

    public SecurityConfig(
            UsuarioRepository usuarioRepository,
            LoginSuccessHandler loginSuccessHandler,
            LogoutHandlerCustom logoutHandlerCustom
    ) {
        this.usuarioRepository = usuarioRepository;
        this.loginSuccessHandler = loginSuccessHandler;
        this.logoutHandlerCustom = logoutHandlerCustom;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        .requestMatchers(
                                "/",
                                "/index",
                                "/login",
                                "/cadastro",
                                "/salvar",
                                "/error"
                        ).permitAll()

                        // SOMENTE ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // ADMIN E SUPORTE
                        .requestMatchers("/categorias/**").hasAnyRole("ADMIN", "SUPORTE")
                        .requestMatchers("/setores/**").hasAnyRole("ADMIN", "SUPORTE")
                        .requestMatchers("/relatorios/**").hasAnyRole("ADMIN", "SUPORTE")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler)
                        .failureUrl("/?error")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(logoutHandlerCustom)
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}