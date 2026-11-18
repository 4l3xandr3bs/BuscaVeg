package buscaveg.com.br.BuscaVeg.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import buscaveg.com.br.BuscaVeg.DTO.ResumoAvaliacaoDTO;
import buscaveg.com.br.BuscaVeg.Entity.Avaliacao;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    Optional<Avaliacao> findByPrato_IdAndUsuario_Id(Long pratoId, Long usuarioId);

    @Query("""
            select new buscaveg.com.br.BuscaVeg.DTO.ResumoAvaliacaoDTO(
                a.prato.id, avg(a.nota), count(a.id))
            from Avaliacao a
            where a.prato.id in :pratoIds
            group by a.prato.id
            """)
    List<ResumoAvaliacaoDTO> resumirPratos(@Param("pratoIds") List<Long> pratoIds);

    @Query("""
            select new buscaveg.com.br.BuscaVeg.DTO.ResumoAvaliacaoDTO(
                a.prato.empresarial.id, avg(a.nota), count(a.id))
            from Avaliacao a
            group by a.prato.empresarial.id
            """)
    List<ResumoAvaliacaoDTO> resumirEstabelecimentos();

    @Modifying
    @Query("delete from Avaliacao a where a.prato.id = :pratoId")
    void deleteByPratoId(@Param("pratoId") Long pratoId);

    @Modifying
    @Query("delete from Avaliacao a where a.prato.empresarial.id = :empresarialId")
    void deleteByEmpresarialId(@Param("empresarialId") Long empresarialId);
}
