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
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
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

    @Column(name = "cnpj", nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(name = "nome_fantasia", nullable = false, length = 100)
    private String nomeFantasia;

    @Column(name = "razao_social", nullable = false, length = 150)
    private String razaoSocial;

    @Column(name = "endereco", nullable = false, length = 200)
    private String endereco;

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumTipoEstabelecimento tipoEspecializacao;

    @Column
    private Float indiceVegFriendly;

    @OneToOne
    @JoinColumn(name = "id_conta", nullable = false)
    private Conta conta;

  @OneToMany(mappedBy = "empresarial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avaliacao> avaliacoes = new ArrayList<>();
}
