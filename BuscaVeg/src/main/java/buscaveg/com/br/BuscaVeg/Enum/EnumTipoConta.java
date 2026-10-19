package buscaveg.com.br.BuscaVeg.Enum;

/**
 * Tipo de conta de acesso ao sistema.
 *
 * - USUARIO: pessoa que busca lugares.
 * - EMPRESA: estabelecimento (restaurante/mercado) que divulga seu negócio.
 *
 * Este valor também vira a "role" (papel) do Spring Security no login
 * (ver CustomUserDetailService), permitindo, no futuro, liberar telas
 * diferentes para cada tipo de conta.
 */
public enum EnumTipoConta {
    USUARIO,
    EMPRESA
}
