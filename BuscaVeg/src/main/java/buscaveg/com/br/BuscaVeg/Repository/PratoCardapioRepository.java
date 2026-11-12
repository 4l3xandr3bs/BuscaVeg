package buscaveg.com.br.BuscaVeg.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import buscaveg.com.br.BuscaVeg.Entity.PratoCardapio;

@Repository
public interface PratoCardapioRepository extends JpaRepository<PratoCardapio, Long> {

    List<PratoCardapio> findByEmpresarialIdOrderByNomeAsc(Long empresarialId);

    List<PratoCardapio> findByEmpresarialIdAndDisponivelTrueOrderByNomeAsc(Long empresarialId);

    List<PratoCardapio> findByEmpresarialId(Long empresarialId);

    void deleteByEmpresarialId(Long empresarialId);
}
