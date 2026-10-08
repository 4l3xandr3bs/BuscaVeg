package buscaveg.com.br.BuscaVeg.Repository;

import buscaveg.com.br.BuscaVeg.Entity.Avaliacao;
import buscaveg.com.br.BuscaVeg.Entity.Empresarial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

   
    List<Avaliacao> findByEmpresarial(Empresarial empresarial);


    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.empresarial = :empresarial")
    Double calcularMediaNotas(Empresarial empresarial);


    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.empresarial = :empresarial")
    Long contarAvaliacoes(Empresarial empresarial);
}
