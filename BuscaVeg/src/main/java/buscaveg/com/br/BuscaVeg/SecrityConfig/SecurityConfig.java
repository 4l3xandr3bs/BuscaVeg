package buscaveg.com.br.BuscaVeg.SecrityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/usuarioCTR/login",
                                "/usuarioCTR/formCadastrarUsuario",
                                "/usuarioCTR/salvarUsuario",
                                "/empresarialCTR/formCadastrarEmpresa",
                                "/empresarialCTR/salvarEmpresa",
                                "/css/**",
                                "/js/**",
                                "/error")
                        .permitAll()

                        .requestMatchers(
                                "/empresarialCTR/minhaEmpresa",
                                "/empresarialCTR/editarEmpresa/**",
                                "/empresarialCTR/alterarEmpresa/**",
                                "/empresarialCTR/deletarEmpresa/**")
                        .hasRole("EMPRESA")

                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage("/usuarioCTR/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            boolean ehEmpresa = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_EMPRESA"));

                            response.sendRedirect(request.getContextPath()
                                    + (ehEmpresa
                                            ? "/empresarialCTR/minhaEmpresa"
                                            : "/empresarialCTR/listarEmpresas"));
                        })
                        .permitAll())

                .logout(logout -> logout
                        .logoutSuccessUrl("/usuarioCTR/login?logout")
                        .permitAll());

        return http.build();
    }
}
