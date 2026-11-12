package buscaveg.com.br.BuscaVeg.Enum;

/**
 * Etiquetas (tags) que descrevem o que um produto ou estabelecimento oferece.
 *
 * Etiquetas aplicadas a pratos para informar compatibilidade e características.
 */
public enum EnumTagAlimentar {

    VEGANO("Vegano"),
    VEGETARIANO("Vegetariano"),
    SEM_GLUTEN("Sem glúten"),
    SEM_LACTOSE("Sem lactose"),
    ORGANICO("Orgânico");

    private final String descricao;

    EnumTagAlimentar(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
