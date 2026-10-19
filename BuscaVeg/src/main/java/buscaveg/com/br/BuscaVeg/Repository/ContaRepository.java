package buscaveg.com.br.BuscaVeg.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import buscaveg.com.br.BuscaVeg.Entity.Conta;

/**
 * CAMADA REPOSITORY = a ÚNICA camada que conversa com o banco de dados.
 *
 * Você não escreve SQL nem implementação: ao estender JpaRepository, o Spring
 * gera sozinho os métodos prontos save(), findById(), findAll(), delete() etc.
 *   JpaRepository<Conta, Long>  ->  <entidade gerenciada, tipo do ID>
 */
@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    // "Método derivado": o Spring lê o NOME do método e monta a consulta
    // sozinho. findByEmail => SELECT * FROM conta WHERE email = ?
    // Optional indica que pode não existir conta com esse e-mail.
    // É usado no login (ver CustomUserDetailService).
    Optional<Conta> findByEmail(String email);

    // Responde apenas "já existe?" (true/false). Usado no cadastro para
    // recusar e-mail repetido com uma mensagem amigável.
    boolean existsByEmail(String email);
}
