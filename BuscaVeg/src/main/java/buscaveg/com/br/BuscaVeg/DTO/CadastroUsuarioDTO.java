package buscaveg.com.br.BuscaVeg.DTO;

import java.time.LocalDate;

import buscaveg.com.br.BuscaVeg.Enum.EnumPerfilAlimentar;
import lombok.Data;


@Data
public class CadastroUsuarioDTO {

    private String nome;

    private String email;

    private String password;

    private LocalDate dataNascimento;

    private EnumPerfilAlimentar perfilAlimentar;

    private Double latitude;

    private Double longitude;
}
