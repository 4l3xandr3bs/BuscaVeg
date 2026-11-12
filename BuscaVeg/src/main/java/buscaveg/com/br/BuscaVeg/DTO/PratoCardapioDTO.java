package buscaveg.com.br.BuscaVeg.DTO;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import buscaveg.com.br.BuscaVeg.Enum.EnumTagAlimentar;
import lombok.Data;

@Data
public class PratoCardapioDTO {

    private String nome;

    private String descricao;

    private BigDecimal preco;

    private Set<EnumTagAlimentar> tags = new LinkedHashSet<>();
}
