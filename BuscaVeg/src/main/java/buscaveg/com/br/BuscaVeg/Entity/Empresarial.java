package buscaveg.com.br.BuscaVeg.Entity;

import buscaveg.com.br.BuscaVeg.Enum.EnumTipoEstabelecimento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * ENTIDADE EMPRESARIAL = o PERFIL de um ESTABELECIMENTO (restaurante ou
 * mercado) cadastrado no BuscaVeg. É o que as pessoas vão encontrar na busca.
 *
 * Assim como Usuario, o login desta empresa fica em uma Conta (@OneToOne).
 *
 * Itens do diagrama de classes que ainda NÃO existem aqui (próximas etapas):
 * horário de funcionamento, protocoloRestricoes, nota média, ItemCardapio,
 * StatusDeTempoReal e Avaliacao. Veja a tabela "Diagrama x código" no README.
 */
@Entity
@Table(name = "empresarial")
@Data
public class Empresarial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresarial")
    private Long id;

    // CNPJ (texto, para não perder zeros à esquerda). Único: não pode haver
    // duas empresas com o mesmo CNPJ. 18 caracteres cabem o formato 00.000.000/0000-00.
    @Column(name = "cnpj", nullable = false, unique = true, length = 18)
    private String cnpj;

    // Nome pelo qual o estabelecimento é conhecido (o que aparece na busca).
    @Column(name = "nome_fantasia", nullable = false, length = 100)
    private String nomeFantasia;

    // Nome jurídico oficial registrado da empresa.
    @Column(name = "razao_social", nullable = false, length = 150)
    private String razaoSocial;

    @Column(name = "endereco", nullable = false, length = 200)
    private String endereco;

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    // Posição no mapa. Comparando com a latitude/longitude do Usuario, será
    // possível listar os lugares mais próximos.
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // RESTAURANTE ou MERCADO (ver EnumTipoEstabelecimento). Gravado como texto.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumTipoEstabelecimento tipoEspecializacao;

    // Nota "quão amigável ao público vegano" o lugar é.
    // NO DIAGRAMA ESSE VALOR É CALCULADO (calcularIndiceVegFriendly), a partir
    // do cardápio. Por isso NÃO é preenchido por formulário: ver AlterarEmpresarialDTO.
    // Enquanto o cálculo não existir, fica vazio (null).
    @Column
    private Float indiceVegFriendly;

    // Conta de login desta empresa (relação 1 para 1). Obrigatória: a coluna
    // "id_conta" não aceita nulo, então uma Empresarial nunca existe sem Conta.
    @OneToOne
    @JoinColumn(name = "id_conta", nullable = false)
    private Conta conta;

    // FUTURO (composição no diagrama: o status pertence à empresa e some com ela):
    // @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    // @JoinColumn(name = "id_status")
    // private StatusDeTempoReal statusDeTempoReal;
}
