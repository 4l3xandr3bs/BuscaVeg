package buscaveg.com.br.BuscaVeg.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import buscaveg.com.br.BuscaVeg.Entity.Empresarial;

/**
 * Acesso ao banco para a entidade Empresarial.
 * save, findById, findAll, delete... já vêm de JpaRepository.
 */
@Repository
public interface EmpresarialRepository extends JpaRepository<Empresarial, Long> {

    // Já existe empresa com este CNPJ? (evita cadastro duplicado)
    boolean existsByCnpj(String cnpj);

    // Encontra a empresa a partir do e-mail da Conta dela.
    // "findByContaEmail" é lido como: Empresarial -> conta -> email.
    // Usado para descobrir "qual é a empresa da pessoa que está logada".
    Optional<Empresarial> findByContaEmail(String email);
}
