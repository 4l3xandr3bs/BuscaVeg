package buscaveg.com.br.BuscaVeg.DTO;

import lombok.Data;


@Data
public class AvaliacaoDTO {

    private Long empresarialId; 
    private Integer nota;       
    private String comentario;   

    public AvaliacaoDTO() {}

    public AvaliacaoDTO(Long empresarialId, Integer nota, String comentario) {
        this.empresarialId = empresarialId;
        this.nota = nota;
        this.comentario = comentario;
    }
}
