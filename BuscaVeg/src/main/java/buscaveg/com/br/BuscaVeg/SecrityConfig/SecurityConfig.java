package buscaveg.com.br.BuscaVeg.SecrityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * CONFIGURAÇÃO DE SEGURANÇA = define QUEM pode acessar CADA página e como
 * funciona o login.
 *
 * Resumo do que esta classe faz:
 *  1. Algumas URLs são públicas (cadastros, login).
 *  2. A área de edição da empresa só aceita contas do tipo EMPRESA.
 *  3. Todas as outras exigem que a pessoa esteja logada.
 *  4. O login usa uma tela própria (/usuarioCTR/login) em vez da tela padrão
 *     do Spring.
 *
 * Como o Spring confere e-mail e senha? Existe um CustomUserDetailService
 * (que busca a Conta no banco) e um PasswordEncoder (BCrypt), ambos @Service/@Bean.
 * O Spring Security os encontra SOZINHO, então não é preciso ligá-los aqui.
 *
 * CSRF: está ATIVO (padrão do Spring). É a proteção contra sites maliciosos que
 * tentam enviar formulários em nome da pessoa logada. Por isso, nos formulários
 * HTML use th:action="@{...}" (o Thymeleaf coloca o token escondido sozinho) e
 * o botão de sair deve ser um <form method="post" th:action="@{/logout}">.
 *
 * Observação: o nome do pacote está escrito "SecrityConfig" (sem o "u").
 * Funciona normalmente, mas pode ser corrigido para "SecurityConfig".
 */
@Configuration
public class SecurityConfig {

    // Bean principal que define a cadeia de filtros de segurança da aplicação
    // (toda requisição passa por esses filtros antes de chegar ao Controller).
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception { // exigência do Spring Security

        http
            .authorizeHttpRequests(auth -> auth

                // 1) Rotas PÚBLICAS: precisam ser liberadas, senão ninguém
                //    conseguiria se cadastrar (nem fazer login).
                .requestMatchers(
                    "/usuarioCTR/login",
                    "/usuarioCTR/formCadastrarUsuario",
                    "/usuarioCTR/salvarUsuario",
                    "/empresarialCTR/formCadastrarEmpresa",
                    "/empresarialCTR/salvarEmpresa",
                    "/css/**",   // '**' = tudo que vier depois dessa pasta
                    "/js/**",
                    "/error"
                ).permitAll()

                .requestMatchers(
                    "/empresarialCTR/minhaEmpresa",
                    "/empresarialCTR/editarEmpresa/**",
                    "/empresarialCTR/alterarEmpresa/**",
                    "/empresarialCTR/deletarEmpresa/**"
                ).hasRole("EMPRESA")

                // 3) Qualquer outra requisição exige estar logado.
                //    ATENÇÃO: a ordem das regras importa; vale a primeira que combinar.
                .anyRequest().authenticated()
            )

            // Formulário de login personalizado
            .formLogin(form -> form
                .loginPage("/usuarioCTR/login")   // tela de login (GET)
                .loginProcessingUrl("/login")     // URL que RECEBE o formulário (POST);
                                                  // deve ser igual ao th:action="@{/login}" do HTML
                // Para onde ir depois de logar, conforme o tipo de conta:
                //  - EMPRESA -> sua própria empresa;
                //  - USUARIO -> lista de estabelecimentos.
                .successHandler((request, response, authentication) -> {
                    boolean ehEmpresa = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_EMPRESA"));

                    response.sendRedirect(request.getContextPath()
                            + (ehEmpresa
                                ? "/empresarialCTR/minhaEmpresa"
                                : "/empresarialCTR/listarEmpresas"));
                })
                .permitAll()
            )

            // Sair do sistema: o Spring cria o POST /logout automaticamente.
            .logout(logout -> logout
                .logoutSuccessUrl("/usuarioCTR/login?logout")
                .permitAll()
            );

        // Constrói e retorna o objeto SecurityFilterChain configurado
        return http.build();
    }
}
