package buscaveg.com.br.BuscaVeg.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "avaliacao_mercado",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_avaliacao_mercado_usuario",
                columnNames = {"id_usuario", "id_empresarial"}))
@Getter
@Setter
public class AvaliacaoMercado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avaliacao_mercado")
    private Long id;

    @Column(name = "nota", nullable = false)
    private int nota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empresarial", nullable = false)
    private Empresarial mercado;
}
