package buscaveg.com.br.BuscaVeg.SecrityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * @Configuration: Informa ao Spring Boot que esta é uma classe de configuração.
 * Durante a inicialização do sistema, o Spring lê esta classe para carregar os componentes (Beans)
 * que serão usados globalmente em toda a aplicação.
 *
 * Mantê-la separada da SecurityConfig deixa o PasswordEncoder disponível para qualquer
 * classe (ContaService e SecurityConfig, por exemplo) sem acoplar ao restante da configuração de segurança.
 */
@Configuration 
public class SecurityBeansConfig { 
    /**
     * @Bean: Diz ao Spring que este método deve ser executado e o objeto retornado por ele 
     * deve ser registrado como um "componente gerenciado" pelo Spring (um Bean).
     * Isso permite que você injete esse `PasswordEncoder` em qualquer outra classe (como no ContaService) 
     * pelo construtor.
     * * @return Uma instância de BCryptPasswordEncoder, que é um dos algoritmos de hash de senha
     * mais seguros e recomendados atualmente. Ele criptografa as senhas usando a técnica de "salting"
     * para que a senha original nunca seja salva em texto limpo no banco de dados.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
