package buscaveg.com.br.BuscaVeg.Excecao;

/**
 * ERRO DE REGRA DE NEGÓCIO = algo que a PESSOA fez errado e que ela pode
 * corrigir (e-mail já cadastrado, campo obrigatório vazio, CNPJ repetido...).
 *
 * Por que existe uma exceção própria? Para o Controller conseguir separar:
 *  - erro esperado, causado pelo preenchimento -> mostramos a mensagem na
 *    própria tela do formulário;
 *  - erro inesperado (bug, banco fora do ar)   -> deixa estourar normalmente.
 *
 * É uma RuntimeException: ao ser lançada dentro de um método @Transactional,
 * o Spring desfaz (rollback) tudo que já tinha sido gravado naquela operação.
 */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
