package buscaveg.com.br.BuscaVeg.Entity;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import buscaveg.com.br.BuscaVeg.Enum.EnumTagAlimentar;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "prato_cardapio")
@Data
public class PratoCardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prato")
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "descricao", nullable = false, length = 500)
    private String descricao;

    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "disponivel", nullable = false)
    private boolean disponivel = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "prato_cardapio_tags",
            joinColumns = @JoinColumn(name = "id_prato"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag_alimentar", nullable = false)
    private Set<EnumTagAlimentar> tags = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empresarial", nullable = false)
    private Empresarial empresarial;
}
