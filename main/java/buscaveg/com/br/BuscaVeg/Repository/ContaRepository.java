package buscaveg.com.br.BuscaVeg.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import buscaveg.com.br.BuscaVeg.Entity.Conta;


@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByEmail(String email);


    boolean existsByEmail(String email);
}
