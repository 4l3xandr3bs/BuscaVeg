package buscaveg.com.br.BuscaVeg.DTO;

import java.time.LocalDate;

import buscaveg.com.br.BuscaVeg.Enum.EnumPerfilAlimentar;
import lombok.Data;

/**
 * DTO (Data Transfer Object) = "caixa de transporte" dos dados do formulário
 * de CADASTRO DE USUÁRIO.
 *
 * Por que não usar a entidade Usuario direto no formulário?
 *  1. O formulário pede dados de DUAS entidades ao mesmo tempo: e-mail e senha
 *     (que ficam em Conta) e nome, nascimento etc. (que ficam em Usuario).
 *  2. Segurança: se o formulário preenchesse a entidade diretamente, alguém
 *     mal-intencionado poderia enviar campos extras (como "id" ou "conta") e
 *     alterar dados que não deveria. O DTO só aceita os campos listados aqui.
 *
 * Os nomes dos atributos precisam ser iguais aos campos do formulário HTML
 * (th:field="*{email}", th:field="*{nome}" ...).
 * A senha chega em texto puro aqui e é criptografada no ContaService.
 */
@Data
public class CadastroUsuarioDTO {

    private String nome;

    private String email;

    private String password;

    private LocalDate dataNascimento;

    private EnumPerfilAlimentar perfilAlimentar;

    private Double latitude;

    private Double longitude;
}
