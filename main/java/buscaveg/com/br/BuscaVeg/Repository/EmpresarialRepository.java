package buscaveg.com.br.BuscaVeg.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import buscaveg.com.br.BuscaVeg.Entity.Empresarial;


@Repository
public interface EmpresarialRepository extends JpaRepository<Empresarial, Long> {
    boolean existsByCnpj(String cnpj);
    Optional<Empresarial> findByContaEmail(String email);
}
