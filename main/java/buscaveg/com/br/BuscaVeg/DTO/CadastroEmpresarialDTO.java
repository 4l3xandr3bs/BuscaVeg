package buscaveg.com.br.BuscaVeg.DTO;

import buscaveg.com.br.BuscaVeg.Enum.EnumTipoEstabelecimento;
import lombok.Data;

/**
 * DTO do formulário de CADASTRO DE EMPRESA (restaurante ou mercado).
 *
 * Reúne os dados de login (e-mail e senha, que viram uma Conta do tipo EMPRESA)
 * e os dados do estabelecimento (que viram uma Empresarial).
 * Veja CadastroUsuarioDTO para entender por que usamos um DTO.
 *
 * Os nomes dos atributos precisam ser iguais aos campos do formulário HTML.
 */
@Data
public class CadastroEmpresarialDTO {

    // Dados de login (vão para a entidade Conta)
    private String email;

    private String password;

    // Dados do estabelecimento (vão para a entidade Empresarial)
    private String cnpj;

    private String nomeFantasia;

    private String razaoSocial;

    private String endereco;

    private String telefone;

    private Double latitude;

    private Double longitude;

    private EnumTipoEstabelecimento tipoEspecializacao;
}
