package buscaveg.com.br.BuscaVeg.Service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import buscaveg.com.br.BuscaVeg.Entity.Conta;
import buscaveg.com.br.BuscaVeg.Repository.ContaRepository;

/**
 * PONTE ENTRE O LOGIN E O BANCO DE DADOS.
 *
 * O Spring Security sabe fazer login, mas não sabe onde as contas estão
 * guardadas. Esta classe ensina isso: dado um "username" (aqui, o e-mail),
 * ela busca a Conta no banco e entrega ao Spring Security os dados que ele
 * precisa (usuário, senha com hash e papel).
 * Depois, é o próprio Spring Security que compara a senha digitada com o hash.
 *
 * Fluxo do login:
 *  1. A pessoa envia e-mail e senha no formulário (POST /login).
 *  2. O Spring Security chama loadUserByUsername(email).
 *  3. Este método busca a Conta; se não existe, lança erro e o login falha.
 *  4. O Spring compara a senha digitada com a senha do banco.
 *  5. Se conferir, a pessoa é redirecionada (ver SecurityConfig).
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final ContaRepository oContaRepository;

    public CustomUserDetailService(ContaRepository oContaRepository) {
        this.oContaRepository = oContaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // "username" é o e-mail digitado no login.
        Conta oConta = oContaRepository.findByEmail(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "Conta não encontrada!"
                    )
                );

        // Converte a nossa Conta para o objeto que o Spring Security entende.
        return User.builder()
                .username(oConta.getEmail())
                .password(oConta.getPassword()) // já é o hash BCrypt
                // roles("USUARIO") ou roles("EMPRESA"): o Spring guarda como
                // "ROLE_USUARIO" / "ROLE_EMPRESA". Útil para restringir telas por tipo de conta.
                .roles(oConta.getTipo().name())
                .build();
    }
}
