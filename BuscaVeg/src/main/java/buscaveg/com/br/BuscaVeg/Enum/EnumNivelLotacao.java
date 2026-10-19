package buscaveg.com.br.BuscaVeg.Enum;

/**
 * Nível de lotação (quão cheio está um estabelecimento), do mais vazio
 * ao mais cheio. A ordem importa: VAZIO < BAIXA < MEDIA < ALTA < LOTADO.
 *
 * STATUS: ainda NÃO é usado por nenhuma entidade. Está preparado para uma
 * funcionalidade futura de informar a lotação dos locais.
 */
public enum EnumNivelLotacao {

    VAZIO,
    BAIXA,
    MEDIA,
    ALTA,
    LOTADO
}
