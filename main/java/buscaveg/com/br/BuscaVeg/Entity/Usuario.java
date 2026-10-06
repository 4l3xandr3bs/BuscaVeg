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

@Table(name = "usuario")
@Entity
@Data
public class Usuario {

   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nome_usuario", nullable = false, length = 50)
    private String nome;

 
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil_alimentar", nullable = false)
    private EnumPerfilAlimentar perfilAlimentar;

   
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @OneToOne
    @JoinColumn(name = "id_conta", nullable = false)
    private Conta conta;
}
