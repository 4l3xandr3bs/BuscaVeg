package buscaveg.com.br.BuscaVeg.DTO;

import buscaveg.com.br.BuscaVeg.Enum.EnumTipoEstabelecimento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

  
  
   @NotBlank(message = "Informe o CNPJ.")
    @Valid
    private String cnpj;

    @NotBlank(message = "Informe o nome da empresa.")
    @Size(max = 100, message = "O nome da empresa deve ter no máximo 100 caracteres.")
    private String nomeFantasia;

    @NotBlank(message = "Informe a razão social.")
    @Size(max = 200, message = "A razão social deve ter no máximo 200 caracteres.")
    private String razaoSocial;

    private String endereco;

    private String telefone;

    private Double latitude;

    private Double longitude;

    private EnumTipoEstabelecimento tipoEspecializacao;
}
