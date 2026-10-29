package buscaveg.com.br.BuscaVeg.Entity;

import buscaveg.com.br.BuscaVeg.Enum.EnumTipoConta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * ENTIDADE CONTA = as CREDENCIAIS DE ACESSO ao sistema (e-mail + senha).
 *
 * @Entity 
 * @Data   
 */
@Entity
@Data
public class Conta {

    // Chave primária. IDENTITY = o próprio banco (MySQL) gera o número
    // sequencial (1, 2, 3...) com AUTO_INCREMENT.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Conta")
    private Long id;

    // E-mail usado como "nome de login". unique = true impede dois cadastros
    // com o mesmo e-mail. nullable = false obriga a preencher.
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    // Aqui fica a senha JÁ CRIPTOGRAFADA (hash BCrypt), nunca a senha pura.
    // A criptografia acontece em ContaService.cadastrarConta().
    // length = 100 é suficiente: um hash BCrypt tem 60 caracteres.
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    // Define se a conta é de USUARIO ou de EMPRESA (ver EnumTipoConta).
    // EnumType.STRING grava o NOME ("USUARIO"/"EMPRESA") no banco, em vez
    // de um número (0/1). Assim, se a ordem do enum mudar, os dados antigos
    // continuam corretos.
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private EnumTipoConta tipo;

}
