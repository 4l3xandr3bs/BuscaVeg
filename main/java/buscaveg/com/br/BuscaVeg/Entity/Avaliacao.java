package buscaveg.com.br.BuscaVeg.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Table(name = "avaliacao")
@Data
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avaliacao")
    private Long id;

  
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

   
    @ManyToOne
    @JoinColumn(name = "id_empresarial", nullable = false)
    private Empresarial empresarial;

    // Nota de 1 a 5 estrelas
    @Column(name = "nota", nullable = false)
    private Integer nota; 

   
    @Column(name = "comentario", length = 500)
    private String comentario;


    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
}