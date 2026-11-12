package buscaveg.com.br.BuscaVeg.DTO;

import java.time.LocalDate;

import buscaveg.com.br.BuscaVeg.Enum.EnumPerfilAlimentar;
import lombok.Data;

@Data
public class PerfilUsuarioDTO {

    private String nome;

    private LocalDate dataNascimento;

    private EnumPerfilAlimentar perfilAlimentar;

    private String endereco;
}
