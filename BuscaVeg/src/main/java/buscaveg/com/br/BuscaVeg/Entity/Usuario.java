package buscaveg.com.br.BuscaVeg.Entity;

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
import java.time.LocalDate;

import buscaveg.com.br.BuscaVeg.Enum.EnumPerfilAlimentar;
import lombok.Data;

/**
 * ENTIDADE USUÁRIO = o PERFIL da pessoa que usa o BuscaVeg para encontrar
 * restaurantes e mercados.
 *
 * Aqui ficam os dados pessoais e de preferência (nome, nascimento, perfil
 * alimentar, localização). O e-mail e a senha NÃO ficam aqui: ficam na
 * entidade Conta, ligada por @OneToOne.
 *
 * @Table  -> nome da tabela no banco ("usuario").
 * @Entity -> esta classe é mapeada para uma tabela.
 * @Data   -> (Lombok) gera getters/setters/toString/equals/hashCode.
 */
@Table(name = "usuario")
@Entity
@Data
public class Usuario {

    // Chave primária gerada automaticamente pelo banco.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    // Nome de exibição (obrigatório, até 50 caracteres).
    @Column(name = "nome_usuario", nullable = false, length = 50)
    private String nome;

    // Data de nascimento (opcional). LocalDate guarda só dia/mês/ano.
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    // Perfil alimentar da pessoa: VEGANO, VEGETARIANO, FLEXITARIANO ou
    // SIMPATIZANTE (ver EnumPerfilAlimentar). Obrigatório.
    // EnumType.STRING grava o texto do enum no banco.
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil_alimentar", nullable = false)
    private EnumPerfilAlimentar perfilAlimentar;

    // Localização (coordenadas) da pessoa. Serve de base para a busca por
    // lugares próximos. Opcionais.
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // Ligação 1 para 1 com a Conta de login. No banco vira a coluna
    // "id_conta" desta tabela, apontando para a tabela conta.
    // nullable = false: não existe Usuario sem Conta.
    // Não há "cascade": a Conta precisa ser salva ANTES de ser associada
    // (é o que o UsuarioService faz, dentro de uma transação).
    @OneToOne
    @JoinColumn(name = "id_conta", nullable = false)
    private Conta conta;
}
