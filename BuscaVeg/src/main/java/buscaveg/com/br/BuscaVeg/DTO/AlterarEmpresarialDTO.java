package buscaveg.com.br.BuscaVeg.DTO;

import buscaveg.com.br.BuscaVeg.Enum.EnumTipoEstabelecimento;
import lombok.Data;

/**
 * DTO do formulário de EDIÇÃO de uma empresa já cadastrada.
 *
 * Fica de FORA de propósito (a empresa não consegue alterar pelo formulário):
 *  - e-mail e senha  -> pertencem à Conta (terão tela própria no futuro);
 *  - CNPJ            -> é a identidade da empresa, não deve mudar;
 *  - indiceVegFriendly -> é CALCULADO pelo sistema (ver diagrama de classes),
 *                         a empresa não pode digitar a própria nota;
 *  - id e conta      -> nunca devem vir do navegador.
 */
@Data
public class AlterarEmpresarialDTO {

    private String nomeFantasia;

    private String razaoSocial;

    private String endereco;

    private String telefone;

    private Double latitude;

    private Double longitude;

    private EnumTipoEstabelecimento tipoEspecializacao;
}
